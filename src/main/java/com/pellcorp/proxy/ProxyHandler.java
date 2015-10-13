package com.pellcorp.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pellcorp.proxy.Event.Method;

public class ProxyHandler extends AbstractHandler {
    private final AtomicLong messageIdProvider = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final EventHandler handler;
    private final URI target;
    private final Keystore trustStore;
    
    public ProxyHandler(URI target, Keystore trustStore, final EventHandler handler) {
        this.target = target;
        this.trustStore = trustStore;
        this.handler = handler;
    }
    
    @Override
    public void handle(String ctx, Request request, HttpServletRequest httpServletRequest,
            HttpServletResponse response) throws IOException, ServletException {
        try {
            String method = request.getMethod();
            if (method.equalsIgnoreCase("GET")) {
                URI requestUrl = new URI(target.toString() + ctx);
                URIBuilder builder = new URIBuilder(requestUrl);
                for (String name : IterableEnumeration.iterable((Enumeration<String>) request.getParameterNames())) {
                    builder.addParameter(name, request.getParameter(name));
                }
                HttpGet httpGet = new HttpGet(builder.build());
                ContentType type = ContentType.parse(request.getContentType());
                long messageId = messageIdProvider.incrementAndGet();
                Event event = new Event(messageId, requestUrl, Method.GET);
                event.getRequest().setContentType(type);
                
                copyHeaders(event.getRequest(), request, httpGet);
                
                CloseableHttpClient client = createClient();
                CloseableHttpResponse httpResponse = client.execute(httpGet);
                handleResponse(event, response, httpResponse);
                
                handler.handle(event);
                request.setHandled(true);
            } else if (request.getMethod().equalsIgnoreCase("POST")) {
                
                URI requestUrl = new URI(target.toString() + ctx);
                HttpPost httpPost = new HttpPost(requestUrl);
                
                ByteArrayInputStream is = new ByteArrayInputStream(request.getInputStream());
                long messageId = messageIdProvider.incrementAndGet();
                ContentType type = ContentType.parse(request.getContentType());
                Event event = new Event(messageId, requestUrl, Method.POST);
                event.getRequest().setContentType(type);
                event.getRequest().setBuffer(is.getBuffer());
                
                HttpEntity entity = new InputStreamEntity(is, type);
                httpPost.setEntity(entity);
                
                copyHeaders(event.getRequest(), request, httpPost);
                
                httpPost.setEntity(entity);
                
                CloseableHttpClient client = createClient();
                CloseableHttpResponse httpResponse = client.execute(httpPost);
                handleResponse(event, response, httpResponse);
                
                handler.handle(event);
                request.setHandled(true);
            }
        } catch (Exception e) {
            logger.error("Failed", e);
            throw new IOException(e);
        }
    }
    
    private void copyHeaders(Content content, Request request, HttpMessage httpMessage) {
        for (String name : IterableEnumeration.iterable((Enumeration<String>)request.getHeaderNames())) {
            if (name.equalsIgnoreCase("content-length")) {
                continue;
            }
            
            if (!httpMessage.containsHeader(name)) {
                Header header = new BasicHeader(name, request.getHeader(name));
                httpMessage.setHeader(header);
                content.getHeaders().add(header);
            }
        }
    }
    
    private void handleResponse(Event event, HttpServletResponse response,
            CloseableHttpResponse httpResponse) throws IOException {
        try {
            for (Header header : httpResponse.getAllHeaders()) {
                if (!response.containsHeader(header.getName())) {
                    response.setHeader(header.getName(), header.getValue());
                }
            }
            
            HttpEntity entity = httpResponse.getEntity();
            ByteArrayInputStream is = new ByteArrayInputStream(entity.getContent());
            
            ContentType type = ContentType.parse(response.getContentType());
            event.getResponse().setContentType(type);
            event.getResponse().setBuffer(is.getBuffer());
            
            OutputStream out = response.getOutputStream();
            IOUtils.copy(is, out);
            out.close();
            out.close();
            
        } finally {
            httpResponse.close();
        }
    }

    private CloseableHttpClient createClient() throws IOException {
        try {
            SSLContext sslcontext = new SSLContextBuilder().loadTrustMaterial(
                    trustStore.getFile(), 
                    trustStore.getPassword().toCharArray(),
                         new TrustSelfSignedStrategy())
                 .build();
         
         SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                 sslcontext,
                 new String[] { "TLSv1" },
                 null,
                 SSLConnectionSocketFactory.getDefaultHostnameVerifier());
         
         CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
         return httpclient;
        } catch (Throwable e) {
            logger.error("Failed", e);
            throw new IOException(e);
        }
    }
}

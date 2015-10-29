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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
    private final Keystore keyStore;
    
    public ProxyHandler(URI target, Keystore keyStore, Keystore trustStore, EventHandler handler) {
        this.target = target;
        this.trustStore = trustStore;
        this.keyStore = keyStore;
        this.handler = handler;
    }
    
    @SuppressWarnings("unchecked")
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
                ContentType type = null;
                if (request.getContentType() != null) {
                    type = ContentType.parse(request.getContentType());
                } else {
                    type = ContentType.TEXT_HTML;
                }
                long messageId = messageIdProvider.incrementAndGet();
                Event event = new Event(messageId, requestUrl, Method.GET);
                event.getRequest().setContentType(type);
                event.getRequest().setBuffer(new byte[]{});
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
    
    @SuppressWarnings("unchecked")
    private void copyHeaders(Content content, Request request, HttpMessage httpMessage) {
        for (String name : IterableEnumeration.iterable((Enumeration<String>)request.getHeaderNames())) {
            Header header = new BasicHeader(name, request.getHeader(name));
            content.getHeaders().add(header);
            
            if (name.equalsIgnoreCase("content-length")) {
                continue;
            }

            httpMessage.setHeader(header);
        }
    }
    
    private void handleResponse(Event event, HttpServletResponse response,
            CloseableHttpResponse httpResponse) throws IOException {
        try {
            for (Header header : httpResponse.getAllHeaders()) {
                response.setHeader(header.getName(), header.getValue());
                event.getResponse().getHeaders().add(header);
            }
            
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            event.setStatus(statusCode);
            response.setStatus(statusCode);
            
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                ByteArrayInputStream is = new ByteArrayInputStream(entity.getContent());
                event.getResponse().setBuffer(is.getBuffer());
                OutputStream out = response.getOutputStream();
                IOUtils.copy(is, out);
                out.close();
            } else {
                event.getResponse().setBuffer(new byte[]{});
            }
            
            if (response.getContentType() != null) {
                ContentType type = ContentType.parse(response.getContentType());
                event.getResponse().setContentType(type);
            } else {
                event.getResponse().setContentType(ContentType.TEXT_HTML);
            }
        } finally {
            httpResponse.close();
        }
    }

    private CloseableHttpClient createClient() throws IOException {
        try {
            HttpClientBuilder builder = HttpClientBuilder.create()
                    .disableContentCompression();
            
            if ("https".equals(target.getScheme())) {
                SSLContextBuilder contextBuilder = SSLContextBuilder.create();
                contextBuilder.loadTrustMaterial(trustStore.getFile(), trustStore.getPasswordChars());
                
                if (keyStore != null) {
                    contextBuilder.loadKeyMaterial(keyStore.getFile(), keyStore.getPasswordChars(), keyStore.getPasswordChars());
                }
             
                SSLContext sslcontext = contextBuilder.build();
                
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                     sslcontext,
                     new String[] { "TLSv1" },
                     null,
                     SSLConnectionSocketFactory.getDefaultHostnameVerifier());
             
                builder.setSSLSocketFactory(sslsf).build();
            }
            return builder.build();
        } catch (Throwable e) {
            logger.error("Failed", e);
            throw new IOException(e);
        }
    }
}

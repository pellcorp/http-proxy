package com.pellcorp.proxy;

import java.net.URI;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.pellcorp.proxy.cmd.ProxyServerOptions;

public class ProxyServer {
    private final ProxyServerOptions config;
    private Server jettyServer;
    
    public ProxyServer(ProxyServerOptions config) {
        this.config = config;
    }
    
    public void stop() throws Exception {
        jettyServer.stop();
    }
    
    public boolean start(final EventHandler eventHandler) throws Exception {
        jettyServer = new Server();
        
        if ("https".equals(config.getProxy().getScheme())) {
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(config.getKeystore().getFile().getAbsolutePath());
            sslContextFactory.setKeyStorePassword(config.getKeystore().getPassword());
            
            if (config.isClientMASSL()) {
                sslContextFactory.setTrustStore(config.getTrustStore().getFile().getAbsolutePath());
                sslContextFactory.setTrustStorePassword(config.getTrustStore().getPassword());
                sslContextFactory.setNeedClientAuth(true);
            }
            
            SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslContextFactory);
            sslConnector.setPort(getProxyPort());
            jettyServer.setConnectors(new Connector[]{ sslConnector });
        } else {
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(getProxyPort());
            jettyServer.setConnectors(new Connector[]{ connector });
        }
        
        ProxyHandler handler = new ProxyHandler(
                config.getTarget(), config.getClientKeystore(), config.getTrustStore(), eventHandler);
        jettyServer.setHandler(handler);
        
        try {
            jettyServer.start();
            jettyServer.join();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private int getProxyPort() {
        int port = config.getProxy().getPort();
        if (port == -1) {
            if ("https".equals(config.getProxy().getScheme())) {
                return 443;
            } else {
                return 80;
            }
        } else {
            return port;
        }
    }
}

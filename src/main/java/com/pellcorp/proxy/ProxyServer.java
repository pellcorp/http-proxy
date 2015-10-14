package com.pellcorp.proxy;

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
    
    public void start(final EventHandler eventHandler) throws Exception {
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
            sslConnector.setPort(config.getProxy().getPort());
            jettyServer.setConnectors(new Connector[]{ sslConnector });
        } else {
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(config.getProxy().getPort());
            jettyServer.setConnectors(new Connector[]{ connector });
        }
        
        ProxyHandler handler = new ProxyHandler(
                config.getTarget(), config.getClientKeystore(), config.getTrustStore(), eventHandler);
        jettyServer.setHandler(handler);
        jettyServer.start();
        jettyServer.join();
    }
}

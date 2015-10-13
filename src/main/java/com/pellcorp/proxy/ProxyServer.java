package com.pellcorp.proxy;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pellcorp.proxy.cmd.ProxyServerOptions;

public class ProxyServer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
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
        
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(config.getKeystore().getFile().getAbsolutePath());
        sslContextFactory.setKeyStorePassword(config.getKeystore().getPassword());
        
        if (config.isProxyMASSL()) {
            sslContextFactory.setTrustStore(config.getTrustStore().getFile().getAbsolutePath());
            sslContextFactory.setTrustStorePassword(config.getTrustStore().getPassword());
            sslContextFactory.setNeedClientAuth(true);
        }
        
        SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslContextFactory);
        sslConnector.setPort(config.getProxy().getPort());
        jettyServer.setConnectors(new Connector[]{ sslConnector });
        ProxyHandler handler = new ProxyHandler(
                config.getTarget(), config.getClientKeystore(), config.getTrustStore(), eventHandler);
        jettyServer.setHandler(handler);
        jettyServer.start();
        jettyServer.join();
    }
}

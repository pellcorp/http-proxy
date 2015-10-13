package com.pellcorp.proxy;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class ProxyServer {
    private final ProxyServerConfig config;
    private Server jettyServer;
    
    public ProxyServer(ProxyServerConfig config) {
        this.config = config;
    }
    
    public void stop() throws Exception {
        jettyServer.stop();
    }
    
    public void start(final EventHandler eventHandler) throws Exception {
        jettyServer = new Server();
        
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(config.getKeyStore().getFile().getAbsolutePath());
        sslContextFactory.setKeyStorePassword(config.getKeyStore().getPassword());
        
        if (config.getTrustStore() != null) {
            sslContextFactory.setTrustStore(config.getTrustStore().getFile().getAbsolutePath());
            sslContextFactory.setTrustStorePassword(config.getTrustStore().getPassword());
            sslContextFactory.setNeedClientAuth(true);
        }
        
        sslContextFactory.setIncludeProtocols("TLSv1");
        sslContextFactory.setIncludeCipherSuites(
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_RSA_WITH,_RC4_128_SHA",
                "TLS_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_RSA_WITH_AES_128_CBC_SHA",
                "TLS_RSA_WITH_AES_256_CBC_SHA256", 
                "TLS_RSA_WITH_AES_256_CBC_SHA",
                "SSL_RSA_WITH_RC4_128_SHA");
        
        SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslContextFactory);
        sslConnector.setPort(config.getServer().getPort());
        jettyServer.setConnectors(new Connector[]{ sslConnector });
        ProxyHandler handler = new ProxyHandler(
                config.getTarget(), config.getTrustStore(), config.getClientKeyStore(), eventHandler);
        jettyServer.setHandler(handler);
        jettyServer.start();
        jettyServer.join();
    }
}

package com.pellcorp.proxy;

import java.net.URI;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class JettyServer {
    private final Keystore keyStore;
    private final Keystore trustStore;
    private final URI server;
    private final ProxyHandler proxyHandler;
    
    public JettyServer(
            URI server, Keystore keystore, Keystore trustStore, ProxyHandler proxyHandler) {
        this.server = server;
        this.keyStore = keystore;
        this.trustStore = trustStore;
        this.proxyHandler = proxyHandler;
    }
    
    public void start() throws Exception {
        Server jettyServer = new Server();
        
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStore.getFile().getAbsolutePath());
        sslContextFactory.setKeyStorePassword(keyStore.getPassword());
        sslContextFactory.setTrustStore(trustStore.getFile().getAbsolutePath());
        sslContextFactory.setTrustStorePassword(trustStore.getPassword());
        sslContextFactory.setNeedClientAuth(true);
        
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
        
        SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector(sslContextFactory);
        ssl_connector.setPort(server.getPort());
        jettyServer.setConnectors(new Connector[]{ ssl_connector });
        jettyServer.setHandler(proxyHandler);
        jettyServer.start();
        jettyServer.join();
    }
}

package com.pellcorp.proxy;

import java.io.File;
import java.net.URI;

import org.junit.Test;

public class JettyServerTest {
    @Test
    public void test() throws Exception {
        URI server = new URI("https://localhost:8443");
        URI target = new URI("https://localhost:9448");
        Keystore keyStore = new Keystore(new File("src/test/resources/keystore.jks"), "password");
        Keystore trustStore = new Keystore(new File("src/test/resources/truststore.jks"), "password");
        
        ProxyHandler proxyHandler = new ProxyHandler(target, trustStore, new DefaultEventHandler());
        JettyServer jettyServer = new JettyServer(server, keyStore, trustStore, proxyHandler);
        jettyServer.start();
    }
}

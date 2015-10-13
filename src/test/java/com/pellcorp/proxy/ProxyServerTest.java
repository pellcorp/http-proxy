package com.pellcorp.proxy;

import org.junit.Test;

import com.pellcorp.proxy.cmd.ProxyServerCmd;

public class ProxyServerTest {
    @Test
    public void test() throws Exception {
//        ProxyServerConfig config = new ProxyServerConfig();
//        config.setServer(new URI("https://localhost:8443"));
//        config.setTarget(new URI("https://localhost:9448"));
//        config.setKeyStore(new Keystore(new File("src/test/resources/keystore.jks"), "password"));
//        config.setTrustStore(new Keystore(new File("src/test/resources/truststore.jks"), "password"));
//        config.setClientKeyStore(new Keystore(new File("src/test/resources/client-keystore.jks"), "password"));
//        EventHandler eventHandler = new DefaultEventHandler();
//        ProxyServer jettyServer = new ProxyServer(config);
//        jettyServer.start(eventHandler);
        
        String[] args = new String[]{
                "-proxy",
                "https://localhost:8443",
                "-target",
                "https://localhost:9448",
                "-keyStore",
                "src/test/resources/keystore.jks",
                "-trustStore",
                "src/test/resources/truststore.jks",
                "-clientKeyStore",
                "src/test/resources/client-keystore.jks",
                "-enableProxyMASSL",
                "-enableTargetMASSL",
        };
        ProxyServerCmd.main(args);
    }
}

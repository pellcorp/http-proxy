package com.pellcorp.proxy;

import com.pellcorp.proxy.cmd.ProxyServerCmd;

public class ProxyServerTest {
    public static void main(String[] s) throws Exception {
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
                "-enableClientMASSL",
                "-enableServerMASSL",
        };
        ProxyServerCmd.main(args);
    }
}

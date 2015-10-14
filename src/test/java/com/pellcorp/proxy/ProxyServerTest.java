package com.pellcorp.proxy;

import com.pellcorp.proxy.cmd.ProxyServerCmd;

public class ProxyServerTest {
    public static void main(String[] s) throws Exception {
        String[] args = new String[]{
                "-proxy",
                "http://localhost:8999",
                "-target",
                "http://localhost",
//                "-keyStore",
//                "src/test/resources/keystore.jks",
//                "-trustStore",
//                "src/test/resources/truststore.jks",
//                "-clientKeyStore",
//                "src/test/resources/client-keystore.jks",
//                "-enableClientMASSL",
//                "-enableServerMASSL",
        };
        ProxyServerCmd.main(args);
    }
}

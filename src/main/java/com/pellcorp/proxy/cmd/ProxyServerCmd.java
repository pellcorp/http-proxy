package com.pellcorp.proxy.cmd;

import com.pellcorp.proxy.DefaultEventHandler;
import com.pellcorp.proxy.ProxyServer;
import com.pellcorp.proxy.ProxyServerConfig;

public class ProxyServerCmd {
    public static void main(String[] args) throws Exception {
        ProxyServerOptions options = new ProxyServerOptions(args);
        if (options.isValid()) {
            ProxyServerConfig config = new ProxyServerConfig();
            config.setServer(options.getProxy());
            config.setTarget(options.getTarget());
            config.setTrustStore(options.getTrustStore());
            config.setKeyStore(options.getKeystore());
            config.setClientKeyStore(options.getClientKeyStore());
            
            final ProxyServer server = new ProxyServer(config);
            server.start(new DefaultEventHandler());
        } else {
            System.err.println("Error: " + options.getErrorMessage());
            System.err.println(options.getUsage());
        }
    }
}

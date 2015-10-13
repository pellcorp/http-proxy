package com.pellcorp.proxy.cmd;

import com.pellcorp.proxy.DefaultEventHandler;
import com.pellcorp.proxy.ProxyServer;

public class ProxyServerCmd {
    public static void main(String[] args) throws Exception {
        ProxyServerOptions options = new ProxyServerOptions(args);
        if (options.isValid()) {
            final ProxyServer server = new ProxyServer(options);
            server.start(new DefaultEventHandler());
        } else {
            System.err.println("Error: " + options.getErrorMessage());
            System.err.println(options.getUsage());
        }
    }
}

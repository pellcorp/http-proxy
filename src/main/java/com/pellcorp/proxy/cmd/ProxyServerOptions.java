package com.pellcorp.proxy.cmd;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.URIOptionHandler;

import com.pellcorp.proxy.Keystore;

public class ProxyServerOptions {
    private static final String DEFAULT_PASSWORD = "password";
    
	private final CmdLineParser parser;
	private String errorMessage;

	@Option(name = "-proxy", usage = "Proxy URI", required = true, handler=URIOptionHandler.class)
	private URI proxy;

	@Option(name = "-target", usage = "Target URI", required = true, handler=URIOptionHandler.class)
    private URI target;
	
	@Option(name = "-keyStore", usage = "Proxy Keystore", required = false)
    private File keyStore;
    
    @Option(name = "-keyPass", usage = "Proxy Keystore Password", required = false)
    private String keyPass;
    
    @Option(name = "-trustStore", usage = "Truststore", required = false)
    private File trustStore;
    
    @Option(name = "-trustPass", usage = "Truststore Password", required = false)
    private String trustPass;
    
    @Option(name = "-clientKeyStore", usage = "Client Keystore", required = false)
    private File clientKeyStore;
    
    @Option(name = "-clientKeyPass", usage = "Client Keystore Password", required = false)
    private String clientKeyPass;
    
    @Option(name = "-enableProxyMASSL", usage = "Enable Client to Proxy MA-SSL", required = false)
    private boolean enableProxyMASSL; // requires keyStore and trustStore
    
    @Option(name = "-enableTargetMASSL", usage = "Enable Proxy to Target MA-SSL", required = false)
    private boolean enableTargetMASSL; // requires clientKeystore and trustStore
	
	public ProxyServerOptions(String[] args) {
		parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
			
			if (enableTargetMASSL && clientKeyStore == null) {
			    throw new CmdLineException(parser, "Client Key store required to enable Target MA-SSL");
			}
		} catch (CmdLineException e) {
			this.errorMessage = e.getMessage();
			return;
		}
	}

	public boolean isValid() {
		return errorMessage == null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getUsage() {
		StringWriter writer = new StringWriter();
		parser.printUsage(writer, null);
		return writer.toString();
	}

	public URI getProxy() {
	    return proxy;
	}
	
	public URI getTarget() {
	    return target;
	}
	
	public Keystore getKeystore() {
	    if (keyStore != null) {
	        return new Keystore(keyStore, getPassword(keyPass));
	    } else {
	        return null;
	    }
	}

    public Keystore getTrustStore() {
        if (trustStore != null) {
            return new Keystore(trustStore, getPassword(trustPass));
        } else {
            return null;
        }
    }
    
    public Keystore getClientKeystore() {
        if (clientKeyStore != null && isTargetMASSL()) {
            return new Keystore(clientKeyStore, getPassword(clientKeyPass));
        } else {
            return null;
        }
    }

    public boolean isProxyMASSL() {
        return enableProxyMASSL;
    }
    
    public boolean isTargetMASSL() {
        return enableTargetMASSL;
    }
	
	private String getPassword(String value) {
	    if (value == null) {
	        value = keyPass;
	    }
	    
	    if (value == null) {
	        value = DEFAULT_PASSWORD;
	    }
	    
	    return value;
	}
}
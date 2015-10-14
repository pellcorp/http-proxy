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
	
	@Option(name = "-keyStore", usage = "Server Keystore", required = false)
    private File keyStore;
    
    @Option(name = "-keyPass", usage = "Server Keystore Password", required = false)
    private String keyPass;
    
    @Option(name = "-trustStore", usage = "Truststore", required = false)
    private File trustStore;
    
    @Option(name = "-trustPass", usage = "Truststore Password", required = false)
    private String trustPass;
    
    @Option(name = "-clientKeyStore", usage = "Server Client Keystore", required = false)
    private File clientKeyStore;
    
    @Option(name = "-clientKeyPass", usage = "Server Client Keystore Password", required = false)
    private String clientKeyPass;
    
    @Option(name = "-enableClientMASSL", usage = "Enable Client to Proxy MA-SSL", required = false)
    private boolean enableClientMASSL;
    
    @Option(name = "-enableServerMASSL", usage = "Enable Proxy to Server MA-SSL", required = false)
    private boolean enableServerMASSL;
	
	public ProxyServerOptions(String[] args) {
		parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
			
			if (enableServerMASSL && clientKeyStore == null) {
			    throw new CmdLineException(parser, "Client Key store required to enable Server MA-SSL");
			}
			
			if ("https".equals(getProxy().getScheme()) && keyStore == null) {
			    throw new CmdLineException(parser, "Key store required to configure a https proxy");
			}
			
			if ("https".equals(getTarget().getScheme()) && trustStore == null) {
                throw new CmdLineException(parser, "Trust store required to configure a https target");
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
        if (clientKeyStore != null && isServerMASSL()) {
            return new Keystore(clientKeyStore, getPassword(clientKeyPass));
        } else {
            return null;
        }
    }

    public boolean isClientMASSL() {
        return enableServerMASSL;
    }
    
    public boolean isServerMASSL() {
        return enableServerMASSL;
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
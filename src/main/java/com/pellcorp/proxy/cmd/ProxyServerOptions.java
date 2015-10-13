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
	
	@Option(name = "-trustStore", usage = "Trust Store", required = false)
	private File trustStore;
	
	@Option(name = "-trustStorePass", usage = "Trust Store Password", required = false)
    private String trustStorePass;
	
	@Option(name = "-keyStore", usage = "Trust Store", required = false)
    private File keyStore;
	
	@Option(name = "-keyStorePass", usage = "Keystore Password", required = false)
    private String keyStorePass;
	
	@Option(name = "-clientKeyStore", usage = "Trust Store", required = false)
    private File clientKeyStore;
	
	@Option(name = "-clientKeyStorePass", usage = "Client Keystore Password", required = false)
    private String clientKeyStorePass;

	public ProxyServerOptions(String[] args) {
		parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
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
	        return new Keystore(keyStore, getPassword(keyStorePass));
	    } else {
	        return null;
	    }
	}
	
	public Keystore getTrustStore() {
        if (trustStore != null) {
            return new Keystore(trustStore, getPassword(trustStorePass));
        } else {
            return null;
        }
    }
	
	public Keystore getClientKeyStore() {
        if (clientKeyStore != null) {
            return new Keystore(clientKeyStore, getPassword(clientKeyStorePass));
        } else {
            return null;
        }
    }
	
	private String getPassword(String value) {
	    if (value == null) {
	        value = keyStorePass;
	    }
	    
	    if (value == null) {
	        value = DEFAULT_PASSWORD;
	    }
	    
	    return value;
	}
}
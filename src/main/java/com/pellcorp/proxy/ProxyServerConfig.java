package com.pellcorp.proxy;

import java.net.URI;

public class ProxyServerConfig {
    private Keystore keyStore;
    private Keystore trustStore;
    private Keystore clientKeyStore;
    private URI server;
    private URI target;
    
    public ProxyServerConfig() {
    }
    
    public Keystore getKeyStore() {
        return keyStore;
    }
    
    public void setKeyStore(Keystore keyStore) {
        this.keyStore = keyStore;
    }
    
    public Keystore getTrustStore() {
        return trustStore;
    }
    
    public void setTrustStore(Keystore trustStore) {
        this.trustStore = trustStore;
    }
    
    public Keystore getClientKeyStore() {
        return clientKeyStore;
    }
    
    public void setClientKeyStore(Keystore clientKeyStore) {
        this.clientKeyStore = clientKeyStore;
    }
    
    public URI getServer() {
        return server;
    }
    
    public void setServer(URI server) {
        this.server = server;
    }
    
    public URI getTarget() {
        return target;
    }

    public void setTarget(URI target) {
        this.target = target;
    }
}

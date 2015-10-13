package com.pellcorp.proxy;

import java.io.File;

public class Keystore {
    private final File file;
    private final String password;
    
    public Keystore(File file, String password) {
        this.file = file;
        this.password = password;
    }

    public File getFile() {
        return file;
    }

    public String getPassword() {
        return password;
    }
}

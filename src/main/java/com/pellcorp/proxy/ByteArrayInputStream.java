package com.pellcorp.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

public class ByteArrayInputStream extends java.io.ByteArrayInputStream {
    public ByteArrayInputStream(InputStream is) throws IOException {
        super(IOUtils.toByteArray(is));
    }
    
    public byte[] getBuffer() {
        reset();
        
        try {
            GZIPInputStream ungzipped = new GZIPInputStream(this);
            return IOUtils.toByteArray(ungzipped);
        } catch (IOException e) {
            return buf;
        } finally {
            reset();
        }
    }
}

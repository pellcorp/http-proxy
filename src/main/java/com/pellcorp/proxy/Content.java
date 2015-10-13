package com.pellcorp.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;

public class Content {
    private ContentType contentType;
    private byte[] buffer;
    private final List<Header> headers = new ArrayList<>();
    
    public Content() {
    }
    
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public List<Header> getHeaders() {
        return headers;
    }
}

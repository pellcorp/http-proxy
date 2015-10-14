package com.pellcorp.proxy;

import java.net.URI;

public class Event {
    public enum Method {
        GET, POST
    }
    private final Long eventId;
    private final URI uri;
    private int statusCode;
    private final Method method;
    private final Content request = new Content();
    private final Content response = new Content();
    
    public Event(final Long eventId, URI uri, Method method) {
        this.eventId = eventId;
        this.uri = uri;
        this.method = method;
    }

    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public int getStatus() {
        return statusCode;
    }
    
    public Method getMethod() {
        return method;
    }

    public Long getEventId() {
        return eventId;
    }

    public Content getRequest() {
        return request;
    }

    public Content getResponse() {
        return response;
    }

    public URI getUri() {
        return uri;
    }
}
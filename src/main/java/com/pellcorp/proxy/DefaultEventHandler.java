package com.pellcorp.proxy;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;

public class DefaultEventHandler implements EventHandler {
    @Override
    public void handle(Event event) {
       System.out.println("------------------------ Request (" + event.getEventId() + ") ------------------------");
       handleHeaders(event.getRequest());
       System.out.println(prettyPrintXml(event.getRequest()));
       
       System.out.println("------------------------ Response (" + event.getEventId() + ") ------------------------");
       handleHeaders(event.getResponse());
       System.out.println(prettyPrintXml(event.getResponse()));
    }

    private void handleHeaders(Content content) {
        for (Header header : content.getHeaders()) {
            System.out.println(header.getName() + ": " + header.getValue());
        }
    }
    
    private String prettyPrintXml(Content content) {
        try {
            String xml = new String(content.getBuffer(), "UTF-8");
            if (isXml(content.getContentType())) {
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                    //initialize StreamResult with File object to save to file
                    StreamResult result = new StreamResult(new StringWriter());
                    
                    StreamSource source = new StreamSource(new StringReader(xml)); 
                    transformer.transform(source, result);
                    String xmlString = result.getWriter().toString();
                    return xmlString.trim();
                
            } else {
                return xml;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    private boolean isXml(ContentType type) {
        return type.getMimeType().equals(ContentType.TEXT_XML.getMimeType());
    }

}

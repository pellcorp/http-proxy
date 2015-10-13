package com.pellcorp.proxy;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEventHandler implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(Event event) {
        StringBuilder builder = new StringBuilder();
        builder.append(IOUtils.LINE_SEPARATOR);
        builder.append("------------------------ Request (Message ID: " + event.getEventId() + ") ------------------------");
        builder.append(IOUtils.LINE_SEPARATOR);
        handleHeaders(event.getRequest(), builder);
        builder.append(prettyPrintXml(event.getRequest()));
        builder.append(IOUtils.LINE_SEPARATOR);
        builder.append("------------------------ Response (Message ID: " + event.getEventId() + ") ------------------------");
        builder.append(IOUtils.LINE_SEPARATOR);
        handleHeaders(event.getResponse(), builder);
        builder.append(prettyPrintXml(event.getResponse()));
        logger.info(builder.toString());
    }

    private void handleHeaders(Content content, StringBuilder builder) {
        for (Header header : content.getHeaders()) {
            builder.append(header.getName() + ": " + header.getValue());
            builder.append(IOUtils.LINE_SEPARATOR);
        }
    }

    private String prettyPrintXml(Content content) {
        try {
            String xml = new String(content.getBuffer(), "UTF-8");
            if (isXml(content.getContentType())) {
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(
                        "{http://xml.apache.org/xslt}indent-amount", "2");
                // initialize StreamResult with File object to save to file
                StreamResult result = new StreamResult(new StringWriter());

                StreamSource source = new StreamSource(new StringReader(xml));
                transformer.transform(source, result);
                String xmlString = result.getWriter().toString();
                return xmlString.trim();

            } else {
                return xml;
            }
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    private boolean isXml(ContentType type) {
        return type.getMimeType().equals(ContentType.TEXT_XML.getMimeType());
    }
}

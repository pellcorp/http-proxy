package com.pellcorp.proxy;

import org.apache.http.entity.ContentType;

public class ContentUtils {
    public static final ContentType MULTI_PART_RELATED = ContentType.create("multipart/related");
    
    public static boolean isMtom(ContentType type) {
        if (type.getMimeType().equals(MULTI_PART_RELATED.getMimeType())) {
            String typeParam = type.getParameter("type");
            if ("application/xop+xml".equals(typeParam)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isXml(ContentType type) {
        return type.getMimeType().equals(ContentType.TEXT_XML.getMimeType());
    }
}

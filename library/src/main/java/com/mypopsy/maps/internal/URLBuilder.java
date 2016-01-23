package com.mypopsy.maps.internal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLBuilder {

    private static final String UTF8 = "UTF-8";
    private final String url;
    private StringBuilder query;

    public URLBuilder(String url) {
        this.url = url;
    }

    public URLBuilder appendQuery(String key, Object object) {
        return appendQuery(key, object.toString());
    }

    public URLBuilder appendQuery(String key, String value) {
        if(query == null) query = new StringBuilder();
        if(query.length() > 0) query.append('&');
        query.append(encode(key)).append('=').append(encode(value));
        return this;
    }

    @Override
    public String toString() {
        if(query == null || query.length() == 0) return url;
        return url + '?' + query;
    }

    static private String encode(String text) {
        try {
            return URLEncoder.encode(text, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

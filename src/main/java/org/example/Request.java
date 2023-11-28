package org.example;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private String bodyBytes;
    private String paramstLine;
    private List<NameValuePair> params;
    private Map<String,String> paramsMap = new HashMap<>();

    public Request(String method, String path) throws URISyntaxException {
        this.method = method;
        this.path = path;
//      this.bodyBytes = bodyBytes;
        if (path.length() > 1) {
            this.paramstLine = path.split("\\?")[1];
        } else {
            this.paramstLine = "No Parameters";
        }

        this.params = URLEncodedUtils.parse(new URI(path), "UTF-8");
    }

    public Map<String, String> getParts() {
        for (NameValuePair element : params) {
             paramsMap.put(element.getName(), element.getValue());
        }
        return paramsMap;
    }

    public String getQueryParam(String name) {
        return paramstLine;
    }

    @Override
    public String toString() {
        String str = "";
        for (NameValuePair element : params) {
            str += element.toString() + "\r\n";
        }
        System.out.println(str);
        if (str.length() < 1) {
            str = "No Parameters";
        }
        return str;
    }
}
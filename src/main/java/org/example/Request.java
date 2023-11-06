package org.example;

public class Request {
     String method;
     String path;
     String bodyBytes;

     String param;

    public Request(String method, String path, String param) {
        this.method = method;
        this.path = path;
//      this.bodyBytes = bodyBytes;
        this.param = param;
    }


    @Override
    public String toString() {
        return("Method: " + method + " Path: " + path);
    }

}

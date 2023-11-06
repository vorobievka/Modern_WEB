package org.example;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();
//        server.listen(9999);

        server.addHandler("GET", "/messages", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream out) throws IOException {
                // TODO: handlers code
//              System.out.println("Hello, from GET handler!");
                final var filePath = Path.of(".", "public", "GET.html");
//                System.out.println(filePath);
                final var mimeType = Files.probeContentType(filePath);
//                System.out.println(mimeType);
                final var template = Files.readString(filePath);
//                System.out.println(content.length);
                final var content = template.replace(
                        "{param}",
                        request.param
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            }
        });

        server.addHandler("", "", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream out) throws IOException {
                // TODO: handlers code
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
            }
        });

//        server.addHandler("POST", "/messages", new Handler() {
//            public void handle(Request request, BufferedOutputStream responseStream) {
//                // TODO: handlers code
//                  System.out.println("Hello, from POST handler!");
//                responseStream.write((
//                        "HTTP/1.1 'Hello, from GET handler!'\r\n" +
//                                "Content-Length: 0\r\n" +
//                                "Connection: close\r\n" +
//                                "\r\n"
//                ).getBytes());
//                responseStream.flush();
//                continue;
//            }
//        });

        server.listen(9999);

    }
}
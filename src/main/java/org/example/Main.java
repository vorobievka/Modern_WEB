package org.example;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/messages", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream out) throws IOException {
                // TODO: handlers code

                final var filePath = Path.of(".", "public", "GET.html");

                final var mimeType = Files.probeContentType(filePath);

                final var template = Files.readString(filePath);

                final var content = template.replace(
                        "{param}",
                        request.toString()
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

                System.out.println("Parameter last equals " + request.getParts().get("last"));
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
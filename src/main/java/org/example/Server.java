package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final String GET = "GET";
    public static final String POST = "POST";
    final Set<String> allowedMethods = Set.of(GET, POST);


    static ExecutorService threadPool = Executors.newFixedThreadPool(64);
    private static Handler handlerGET;
    private static Handler defaultHandler;

    public static void listen(int port) {
        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        try (final var serverSocket = new ServerSocket(port)) {
            threadPool.execute(doMessage(serverSocket, validPaths));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Runnable doMessage(ServerSocket serverSocket, List validPaths) throws IOException {
        while (true) {
            try (
                    final var socket = serverSocket.accept();
                    final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final var out = new BufferedOutputStream(socket.getOutputStream());
            ) {
                // read only request line for simplicity
                // must be in form GET /path HTTP/1.1
                final var requestLine = in.readLine();
                final var parts = requestLine.split(" ");

                System.out.println("client connected");

                Request request;

                if(parts[1].split("\\?").length > 1){
                    request = new Request(parts[0], parts[1], parts[1].split("\\?")[1]);
                } else {
                    request = new Request(parts[0], parts[1], "None");
                }

                if (parts[0].equals("GET") && parts[1].split("\\?")[0].equals("/messages")) {
//                  System.out.println("Get Handler");
                    System.out.println(request);
                    handlerGET.handle(request, out);
                    continue;
                } else {
                    defaultHandler.handle(request, out);
                }

                if (parts.length != 3) {
                    // just close socket
                    continue;
                }

                final var path = parts[1];
                if (!validPaths.contains(path)) {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                    continue;
                }

                final var filePath = Path.of(".", "public", path);
                System.out.println(filePath);
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
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
                    continue;
                }

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            }
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (method.equals("GET") && path.equals("/messages")) {
            handlerGET = handler;
        } else {
            defaultHandler = handler;
        }

    }
}

package org.example;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        server.listen(9999);
    }
}
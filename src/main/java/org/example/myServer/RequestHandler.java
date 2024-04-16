package org.example.myServer;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class RequestHandler {
    protected static void extractedException(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(405, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected static void goodAnswer(HttpExchange exchange, String response) throws IOException {
        String message = new StringBuilder().append(response).toString();
        exchange.sendResponseHeaders(200, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

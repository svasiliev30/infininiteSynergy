package org.example.myServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.dao.ProductRepository;

import java.io.IOException;

public class MyServerPostSignup extends RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();
        if ("POST".equals(exchange.getRequestMethod())) {
            String login = null;
            String password = null;
            ;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(exchange.getRequestBody());

                login = jsonNode.get("login").asText();
                password = jsonNode.get("password").asText();
            } catch (Exception e) {
                String response = " error date";
                extractedException(exchange, response);
                e.printStackTrace();
            }
            boolean result = new ProductRepository().addPerson(login, password);
            if (result) {
                String response = "Creating client - " + login;
                goodAnswer(exchange, response);
            } else {
                String response = "We cant creating client - " + login;
                extractedException(exchange, response);

            }
        }
    }
}

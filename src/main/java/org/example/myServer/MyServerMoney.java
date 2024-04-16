package org.example.myServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Cashe.Cashe;
import org.example.dao.ProductRepository;

import java.io.IOException;
import java.rmi.RemoteException;

public class MyServerMoney extends RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().toString();
        if ("POST".equals(exchange.getRequestMethod())) {
            String login = null;
            Double amount = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(exchange.getRequestBody());

                login = jsonNode.get("to").asText();
                amount = Double.valueOf(jsonNode.get("amount").asText());
            } catch (Exception e) {
                String response = " error date";
                extractedException(exchange, response);
                e.printStackTrace();
            }

            boolean result = new ProductRepository().authenticationPerson(login, null, url);
            if (!result) {
                String response = login + " you not authentication ";
                extractedException(exchange, response);
                throw new RemoteException("stop program");
            }
            result = new ProductRepository().sendSum(login, amount);
            if (result) {
                String response = " your send sum - " + login;
                goodAnswer(exchange, response);
            }
        }
        if ("GET".equals(exchange.getRequestMethod())) {
            boolean result = new ProductRepository().authenticationPerson(null, null, url);
            if (!result) {
                String response = " you not authentication ";
                extractedException(exchange, response);
                throw new RemoteException("stop program");
            }
            Double amount = new ProductRepository().getSum();

            String response = " your amount - " + amount;
            goodAnswer(exchange, response);
        }
    }
}

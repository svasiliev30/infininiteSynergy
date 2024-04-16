package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.dao.ProductRepository;
import org.example.myServer.MyServerMoney;
import org.example.myServer.MyServerPostSignin;
import org.example.myServer.MyServerPostSignup;
import org.example.pojo.Person;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {

        int port = 8080;
            HttpServer server = HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/signup", new MyServerPostSignup());
        server.createContext("/signin", new MyServerPostSignin());
        server.createContext("/money", new MyServerMoney());
        server.setExecutor(Executors.newSingleThreadExecutor());

        server.start();
        System.out.println(server.getAddress().getAddress().getHostAddress());
        System.out.println(server.getAddress().getAddress().getHostName());

        ////created table
//        System.out.println(new ProductRepository().createTable());

    }
}
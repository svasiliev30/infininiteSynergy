package org.example.dao;

import org.example.Cashe.Cashe;
import org.example.Logs.Log;
import org.example.Logs.LogWriter;
import org.example.pojo.Person;
import org.example.token.TokenImpl;

import java.sql.*;
import java.time.LocalDateTime;

public class ProductRepository {
    String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
    String name = "postgres";
    String password = "12345";
    TokenImpl token = new TokenImpl();
    Person localPerson;
    LogWriter log = new Log();


    public boolean createTable() {
        String sql = "CREATE TABLE bank (" +
                "id SERIAL PRIMARY KEY," +
                "login VARCHAR(150) NOT NULL UNIQUE," +
                "password VARCHAR(150) NOT NULL," +
                "amount INT," +
                "token VARCHAR(150)" +
                ");";

        try (Connection connection = DriverManager.getConnection(this.jdbcUrl, this.name, this.password);
             Statement statement = connection.createStatement()) {
            int resultCreated = statement.executeUpdate(sql);
            System.out.println("result created table -" + resultCreated);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean addPerson(String login, String password) {
        String tokenKod = token.createToken(login, password);
        try (Connection connection = DriverManager.getConnection(this.jdbcUrl, this.name, this.password);
             Statement statement = connection.createStatement()) {

            String sql = String.format("INSERT INTO bank ( login, password, amount, token ) VALUES ('%s', '%s', %s, '%s')",
                    login, password, 0, tokenKod);
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        log.logWrite(LocalDateTime.now() + " Create account login = " + login);
        if (authenticationPerson(login, password, null)) return true;
        return false;
    }

    public boolean authenticationPerson(String login, String password, String url) {
        if (url.equals("/money")) {
            try {
                String cashToken = Cashe.person.getToken();
                if (!token.validateToken(cashToken)) return false;
                return true;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }

        }
        String sqlGetPerson = "SELECT * from bank" +
                " WHERE login ='" + login + "' AND password ='" + password + "'";


        try (Connection connection = DriverManager.getConnection(this.jdbcUrl, this.name, this.password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlGetPerson)) {
            resultSet.next();

            localPerson = new Person(
                    resultSet.getInt("id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getDouble("amount"),
                    resultSet.getString("token"));
            Cashe.person = localPerson;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (url.equals("/signin")) {
            if (authenticationPersonAndNewToken(login, password)) return true;
            return false;
        }
        if (!token.validateToken(localPerson.getToken())) return false;
        log.logWrite(LocalDateTime.now() + " Authentication account login = " + login);
        return true;
    }

    public boolean authenticationPersonAndNewToken(String login, String password) {
        try (Connection connection = DriverManager.getConnection(this.jdbcUrl, this.name, this.password);
             Statement statement = connection.createStatement();) {
            String newToken = token.createToken(login, password);
            localPerson.setToken(newToken);
            Cashe.person = localPerson;
            statement.executeUpdate("UPDATE bank SET token = '" + newToken +
                    "' WHERE login = '" + login + "' AND password = '" + password + "'");

            if (!token.validateToken(localPerson.getToken())) return false;
            log.logWrite(LocalDateTime.now() + " Authentication account login = " + login);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendSum(String login, double sum) {
        String sqlGetPerson = "SELECT * from bank " +
                "WHERE login ='" + login + "'";


        try (Connection connection = DriverManager.getConnection(this.jdbcUrl, this.name, this.password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlGetPerson)) {
            if (sum < 0) return false;
            resultSet.next();

            Person bdPerson = new Person(
                    resultSet.getInt("id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"),
                    resultSet.getDouble("amount"),
                    resultSet.getString("token"));

            double resultSum = bdPerson.getAmount() + sum;
            bdPerson.setAmount(resultSum);

            statement.executeUpdate("UPDATE bank SET amount = " + bdPerson.getAmount() +
                    " WHERE login = '" + login + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        log.logWrite(LocalDateTime.now() + " User " + Cashe.person.getLogin() + " received the amount = " + sum + " user " + login);
        return true;
    }

    public Double getSum() {

        log.logWrite(LocalDateTime.now() + " User " + Cashe.person.getLogin() + " sea sum = " + Cashe.person.getAmount());
        return Cashe.person.getAmount();

    }
}

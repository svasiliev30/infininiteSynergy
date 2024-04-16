package org.example.pojo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Person {
    int id;
    String login;
    String password;
    double amount;
    String token;
}

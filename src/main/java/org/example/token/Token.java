package org.example.token;

import io.jsonwebtoken.Claims;

public interface Token {
    public String createToken(String login,String password);
    public Claims parseToken(String token);
    public Boolean validateToken(String token);
}

package org.example.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class TokenImpl implements Token {
    private String secretKey = "mySecretKey123";
    private  long validityInMilliseconds = 3600000;

    @Override
    public String createToken(String login,String password) {
        String token = null;
        try {
            token = Jwts.builder()

                    .setSubject(login)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return token;
    }

    @Override
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJwt(token)
                .getBody();
    }

    @Override
    public Boolean validateToken(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            if (expiration.before(new Date())) return false;
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

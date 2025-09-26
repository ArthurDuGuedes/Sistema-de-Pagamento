package com.sistemaPagamento.sistemaDePagamento.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sistemaPagamento.sistemaDePagamento.entity.User;
@Service
public class TokenService {
  @Value("${jwt.secret}")
  private String secret;

  public String generateToken(User user){
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      String token  = JWT.create().withIssuer("auth")
        .withSubject(user.getEmail()).withExpiresAt(ExpirationDate())
        .sign(algorithm);
      return token;
    } catch (JWTCreationException e) {
      throw new RuntimeException("Erro ao gerar token" + e );
    }
  }

  public String validadeToken(String token){
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm)
          .withIssuer("auth")
          .build().verify(token).getSubject();
    } catch (JWTVerificationException e){
      throw new RuntimeException("Token inválido" + e);
    }
  }

  private Instant ExpirationDate(){
    return LocalDateTime.now().plusMinutes(5)
        .toInstant(ZoneOffset.of("-03:00"));
  }
}

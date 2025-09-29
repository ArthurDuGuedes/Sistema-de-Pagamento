package com.sistemaPagamento.sistemaDePagamento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaPagamento.sistemaDePagamento.dto.AuthenticationRequest;
import com.sistemaPagamento.sistemaDePagamento.dto.AuthenticationResponse;
import com.sistemaPagamento.sistemaDePagamento.entity.User;
import com.sistemaPagamento.sistemaDePagamento.services.TokenService;

@RestController
@RequestMapping("/auth")
public class LoginController {

  @Autowired
  private TokenService tokenService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest){

      var usernamePassword = new UsernamePasswordAuthenticationToken(
        authenticationRequest.email(),
        authenticationRequest.password()
      );
      var auth = authenticationManager.authenticate(usernamePassword);
      var token = tokenService.generateToken((User)auth.getPrincipal());

      return ResponseEntity.ok(new AuthenticationResponse(token));
      
    }
}

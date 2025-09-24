package com.sistemaPagamento.sistemaDePagamento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaPagamento.sistemaDePagamento.services.UserService;
import com.sistemaPagamento.sistemaDePagamento.dto.UserRequest;
import com.sistemaPagamento.sistemaDePagamento.entity.User;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userServices;

  @PostMapping
  public ResponseEntity<User> RegisterUser(@RequestBody UserRequest userRequest){

    User user = userRequest.toModel();
    userServices.registerUser(user);
    return ResponseEntity.ok().body(userSaved);
    
  }
}

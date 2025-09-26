package com.sistemaPagamento.sistemaDePagamento.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaPagamento.sistemaDePagamento.services.UserService;
import com.sistemaPagamento.sistemaDePagamento.dto.UserRequest;
import com.sistemaPagamento.sistemaDePagamento.dto.UserResponse;
import com.sistemaPagamento.sistemaDePagamento.entity.User;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;



import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userServices;

  @PostMapping
  public ResponseEntity<UserResponse> RegisterUser(@RequestBody @Valid UserRequest userRequest) throws MessagingException, UnsupportedEncodingException{
    
    User user = userRequest.toModel();
    UserResponse userSaved = userServices.registerUser(user);
    return ResponseEntity.ok().body(userSaved);
    
  }

  @GetMapping("/verify")
  public String verifyUser(@Param("code") String code){
    if(userServices.verify(code)){
      return "verify_success";
    }else{
      return "verify_error";
    }
  }

  @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userServices.getAllUsers();
        return ResponseEntity.ok(users);
    }

}

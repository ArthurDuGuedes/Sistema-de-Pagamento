package com.sistemaPagamento.sistemaDePagamento.dto;

import com.sistemaPagamento.sistemaDePagamento.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest( 
  @NotNull(message = "Name is required") 
  @NotBlank(message = "Name is required") 
  
  String name, 

  @NotNull(message = "Email is required")
  @NotBlank(message = "Email is required")
  String email, 

  @NotNull(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Size(min=8, message = "Password must be at least 8 characters")
  String password ) {

  public User toModel(){
    return new User(null, this.name, this.email, this.password, null, false);
  }

}

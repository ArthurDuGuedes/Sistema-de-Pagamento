package com.sistemaPagamento.sistemaDePagamento.dto;

import com.sistemaPagamento.sistemaDePagamento.entity.User;

public record UserRequest( String name, String email, String password ) {

  public User toModel(){
    return new User(name,email,password);
  }

}

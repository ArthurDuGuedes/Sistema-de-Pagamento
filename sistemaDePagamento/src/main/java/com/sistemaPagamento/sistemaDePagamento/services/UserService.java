package com.sistemaPagamento.sistemaDePagamento.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistemaPagamento.sistemaDePagamento.entity.User;
import com.sistemaPagamento.sistemaDePagamento.repository.UserRepository;
import com.sistemaPagamento.sistemaDePagamento.uteis.RandomString;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public User registerUser(User user){
    if(userRepository.findByEmail(user.getEmail()) != null){
      throw new RuntimeException("This email already exists");

    }else{
      String encodedPassword = passwordEncoder.encode(user.getPassword());
      user.setPassword(encodedPassword);

      String randomCode = RandomString.generateRandomString(64);
      user.setVerificationCode(randomCode); 
      user.setEnabled(false);

      User savedUser = userRepository.save(user);

      return savedUser;
    }
  }
}

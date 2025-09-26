package com.sistemaPagamento.sistemaDePagamento.services;

import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistemaPagamento.sistemaDePagamento.dto.UserResponse;
import com.sistemaPagamento.sistemaDePagamento.entity.User;
import com.sistemaPagamento.sistemaDePagamento.repository.UserRepository;
import com.sistemaPagamento.sistemaDePagamento.uteis.RandomString;

import jakarta.mail.MessagingException;


import java.util.List;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MailService mailService;

  public UserResponse registerUser(User user) throws MessagingException, UnsupportedEncodingException {
    if(userRepository.findByEmail(user.getEmail()) != null){
      throw new RuntimeException("This email already exists");

    }else{
      String encodedPassword = passwordEncoder.encode(user.getPassword());
      user.setPassword(encodedPassword);

      String randomCode = RandomString.generateRandomString(64);
      user.setVerificationCode(randomCode); 
      user.setEnabled(false);

      User savedUser = userRepository.save(user);
      UserResponse userResponse = UserResponse.fromModel(savedUser);  
      mailService.senderVerificationEmail(user);
      return userResponse;
    }
  }
  public boolean verify(String verificationCode){
    User user = userRepository.findByVerificationCode(verificationCode);

    if(user == null || user.isEnabled()){
      return false;
    }else{
      user.setVerificationCode(null);
      user.setEnabled(true);
      userRepository.save(user);

      return true;
    }
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(UserResponse::fromModel)
        .collect(Collectors.toList());
  }
}

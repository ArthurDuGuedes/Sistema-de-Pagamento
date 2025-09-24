package com.sistemaPagamento.sistemaDePagamento.uteis;

import java.security.SecureRandom;

public class RandomString { 
  private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  public static String generateRandomString(int length){
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < length; i++) {
      int index = random.nextInt(CHARACTERS.length());
      char randomChar = CHARACTERS.charAt(index);
      sb.append(randomChar);
    }
    return sb.toString();
  }


}

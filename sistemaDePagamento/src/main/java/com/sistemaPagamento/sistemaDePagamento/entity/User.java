package com.sistemaPagamento.sistemaDePagamento.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="users")
@Table(name="users")

public class User implements UserDetails{

  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE)

  private Long id;

  private String name;

  private String email;

  private String password;
  
  private String verificationCode;

  private boolean enabled;


  public User(Long id, String name, String email, String password, String verificationCode, boolean enabled) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.verificationCode = verificationCode;
    this.enabled = enabled;
  }

  public User() {
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
      return null;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }   

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
  
}

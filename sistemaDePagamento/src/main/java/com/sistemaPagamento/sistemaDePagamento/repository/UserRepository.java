package com.sistemaPagamento.sistemaDePagamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.sistemaPagamento.sistemaDePagamento.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByEmail(String email);
    User findByVerificationCode(String verificationCode);
}
package com.sistemaPagamento.sistemaDePagamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sistemaPagamento.sistemaDePagamento.config.SecurityConfig;

@SpringBootApplication
// @EnableAutoConfiguration(exclude = { SecurityConfig.class})
public class SistemaDePagamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDePagamentoApplication.class, args);
	}

}

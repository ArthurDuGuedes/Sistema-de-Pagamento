package com.sistemaPagamento.sistemaDePagamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
// @EnableAutoConfiguration(exclude = { SecurityConfig.class})
public class SistemaDePagamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDePagamentoApplication.class, args);
	}

}

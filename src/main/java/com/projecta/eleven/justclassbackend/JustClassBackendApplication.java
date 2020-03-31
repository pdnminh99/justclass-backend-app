package com.projecta.eleven.justclassbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class JustClassBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JustClassBackendApplication.class, args);
	}

	@GetMapping
	public String defaultHandler() {
		return "<h1>It works!</h1>";
	}
}

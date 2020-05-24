package com.projecta.eleven.justclassbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableScheduling
public class Application {

	@RequestMapping(value = "_ah/warmup", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public void handleWarmUpRequest() {
		// Do nothing.
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

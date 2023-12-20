package com.funnyproject.todolisttaskapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class TodolistApiApplication {


	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(TodolistApiApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", System.getProperty("TASK_API_PORT")));

		app.run(args);
	}

}

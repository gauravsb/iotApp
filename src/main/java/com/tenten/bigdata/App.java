package com.tenten.bigdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class App {
	public static void main(String[] args) {
		log.info("Start Data API application...............");
		SpringApplication.run(App.class, args);
	}
}

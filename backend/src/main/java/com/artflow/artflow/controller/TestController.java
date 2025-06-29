package com.artflow.artflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	private class HelloDto {
		String greeting;
		
		public HelloDto(String greeting) {
			this.greeting = greeting;
		}
		
		public String getGreeting() {
			return greeting;
		}
		
		public void setGreeting(String greeting) {
			this.greeting = greeting;
		}
	}
	
	@GetMapping("/api/hello")
	public ResponseEntity<HelloDto> hello() {
		return ResponseEntity.ok(new HelloDto("yo"));
	}
}

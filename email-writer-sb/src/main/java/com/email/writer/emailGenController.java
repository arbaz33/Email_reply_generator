package com.email.writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")

public class emailGenController {
	
	private final EmailGenService emailGenService;
	
	public emailGenController(EmailGenService emailGenService) {
		super();
		this.emailGenService = emailGenService;
	}

	
	@PostMapping("/generate")
	public ResponseEntity<String> genaerateEmail(@RequestBody EmailRequest emailRequest){
		String response = emailGenService.generateEmailReply(emailRequest);
		return ResponseEntity.ok(response);
	}

}

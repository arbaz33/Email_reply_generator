package com.email.writer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class EmailGenService {
	
	private final WebClient webClient;
	
	public EmailGenService(WebClient.Builder webClientBuilder) {
		super();
		this.webClient = webClientBuilder.build();
	}


	@Value("${gemini.api.url}")
	private String geminiApiUrl;
	
	@Value("${gemini.api.key}")
	private String geminiApiKey;
	
	
	public String generateEmailReply(EmailRequest emailRequest) {
		String prompt = buildPromt(emailRequest);
		// request format for prompt from gemini
		
		Map<String, Object> requestBody = Map.of(
				"contents", new Object[]{
					Map.of("parts", new Object[]{
						Map.of("text", prompt)
					 }
					)		
				}
		);
		
		// do a request and get a response
		
		String response = webClient.post()
				.uri(geminiApiUrl + geminiApiKey)
				.header("Content-Type", "application/json")
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		
		//Extract response and return 
		return extractResponseContent(response);
		
	
		
	}
	
	
	public String extractResponseContent(String response) {
		
		try {
			ObjectMapper mapper  = new ObjectMapper();
			JsonNode rootNode =  mapper.readTree(response);
			return rootNode.path("candidates")
					.get(0)
					.path("content")
					.path("parts")
					.get(0)
					.path("text")
					.asText();
			
		}catch(Exception e) {
			return "Erro processing request: "+e.getMessage();
		}
		
	}
	
	
	public String buildPromt(EmailRequest emailRquest) {
		
		StringBuilder prompt = new StringBuilder();
		
		prompt.append("generate a professional email response for the following email content. Dont generate the subject line  ");
		
		if(emailRquest.getTone() != null && !emailRquest.getTone().isEmpty()) {
			prompt.append("Use a ").append(emailRquest.getTone()).append("tone");
		}
		prompt.append("\n Original email : \n").append(emailRquest.getEmailContent());
		return prompt.toString();
		
	}

}

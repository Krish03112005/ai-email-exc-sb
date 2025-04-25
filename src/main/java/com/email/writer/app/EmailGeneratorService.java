package com.email.writer.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webclient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webclientBuilder) {
        this.webclient = webclientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest) {
        // Log the incoming request (for debugging)
        System.out.println("\n=== Received Email Request ===");
        System.out.println("Email Content: " + emailRequest.getEmailContent());
        System.out.println("Tone: " + emailRequest.getTone());

        // Build the prompt
        String prompt = buildPrompt(emailRequest);

        // Log the constructed prompt
        System.out.println("\n=== Sending to Gemini ===");
        System.out.println("Prompt: " + prompt);

        // Craft a request
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        // Log the request body (optional)
        System.out.println("Request Body: " + requestBody);

        // Do request and get Request
        String response = webclient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Log the raw API response
        System.out.println("\n=== Raw Gemini Response ===");
        System.out.println(response);

        // Process and return the response
        String extractedResponse = extractResponseContent(response);
        System.out.println("\n=== Extracted Reply ===");
        System.out.println(extractedResponse);

        //Return Response and return
        return extractedResponse;
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            System.err.println("Error processing response: " + e.getMessage());
            return "Error Processing request: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line ");
        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();

    }
}

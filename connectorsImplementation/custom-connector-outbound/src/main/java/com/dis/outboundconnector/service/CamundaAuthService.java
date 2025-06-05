package com.dis.outboundconnector.service;



import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CamundaAuthService implements CommandLineRunner {

    @Value("${camunda.client.auth.client-id}")
    private String clientId;

    @Value("${camunda.client.auth.client-secret}")
    private String clientSecret;

    @Value("${camunda.client.identity.base-url}")
    private String authUrl;

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("audience", "tasklist.camunda.io"); 

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);

        String token = response.getBody().get("access_token").toString();

        System.out.println("✅ Access Token: " + token);
        return token;
    }
  
    
    public String getZeebAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("audience", "zeebe.camunda.io"); 

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);

        String token = response.getBody().get("access_token").toString();

        System.out.println("✅ Access Token: " + token);
        return token;
    }

    // This method runs once the Spring Boot app is started
    @Override
    public void run(String... args) {
        try {
            getAccessToken(); 
            getZeebAccessToken();// Trigger token fetch at startup
        } catch (Exception e) {
            System.err.println("❌ Error fetching token at startup: " + e.getMessage());
        }
    }
}

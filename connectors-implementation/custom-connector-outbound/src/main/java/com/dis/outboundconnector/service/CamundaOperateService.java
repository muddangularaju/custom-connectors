package com.dis.outboundconnector.service;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CamundaOperateService {

	@Value("${camunda.client.tasklist.base-url}")
	private String tasklisturl;

	@Value("${camunda.client.zeebe.base-url}")
	private String zeebeurl;

	@Autowired
	private final CamundaAuthService authService;

	public CamundaOperateService(CamundaAuthService authService) {
		this.authService = authService;
	}

	public String getActiveInstances(Map<String, Object> variables) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authService.getAccessToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		String queryJson = "";
		try {
			queryJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(variables);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpEntity<String> request = new HttpEntity<>(queryJson, headers);
		String endpoint = tasklisturl + "/v1/tasks/search";

		ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);
		return response.getBody();
	}

	public String getTaskVariables(String taskId, Map<String, Object> body) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(authService.getAccessToken());
			headers.setContentType(MediaType.APPLICATION_JSON);

			String requestBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);
			HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

			String url = tasklisturl + "/v1/tasks/" + taskId + "/variables/search";
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			return response.getBody();

		} catch (Exception e) {
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

	public String completeTask(String taskId, Map<String, Object> variables) {
		try {
			// Use default RestTemplate (no need for PATCH support anymore)
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(authService.getZeebAccessToken());
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> requestBodyMap = new HashMap<>();
			requestBodyMap.put("variables", variables); // already a Map<String, Object>
			requestBodyMap.put("action", "complete");   // optional but recommended

			String requestBody = new ObjectMapper().writeValueAsString(requestBodyMap);
			HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
			String url = zeebeurl + "/user-tasks/" + taskId + "/completion";

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			return response.getBody();

		} catch (Exception e) {
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

}

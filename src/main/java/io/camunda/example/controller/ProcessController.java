package io.camunda.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@RestController
@RequestMapping("/api")
public class ProcessController {

	@Autowired
	private ZeebeClient zeebeClient;

	@PostMapping("/start-process")
	public String startProcess(
			@RequestParam("bpmnProcessId") String bpmnProcessId, 
			@RequestParam("input1") String input1,
			@RequestParam("input2") String input2) {
		// Create variables map
		Map<String, Object> variables = new HashMap<>();
		variables.put("input1", input1);
		variables.put("input2", input2);

		// Start the process instance
		ProcessInstanceEvent processInstance = zeebeClient.newCreateInstanceCommand()
				.bpmnProcessId(bpmnProcessId)
				.latestVersion()
				.variables(variables)
				.send()
				.join();

		return "Process started with key: " + processInstance.getProcessInstanceKey();
	}
}

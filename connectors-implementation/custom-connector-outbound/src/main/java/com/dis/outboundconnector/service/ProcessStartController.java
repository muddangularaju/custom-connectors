package com.dis.outboundconnector.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@RestController
@RequestMapping("/api/process")
public class ProcessStartController {
	  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessStartController.class);

    private ZeebeClient zeebeClient;
    private final CamundaOperateService operateService;

    @Autowired
    public ProcessStartController(ZeebeClient zeebeClient, CamundaOperateService operateService) {
        this.zeebeClient = zeebeClient;
        this.operateService = operateService;
    }

    @PostMapping("/start")
    public Map<String, Object> startProcess(@RequestParam("processId") String processId,
                                           @RequestBody(required = false) Map<String, Object> variables) {

        ProcessInstanceEvent instance = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processId)
                .latestVersion()
                .variables(variables != null ? variables : Map.of())
                .send()
                .join();
        LOGGER.info("process started successfully");

        return Map.of(
                "processInstanceKey", instance.getProcessInstanceKey(),
                "bpmnProcessId", instance.getBpmnProcessId(),
                "version", instance.getVersion()
        );
        
    }

    @PostMapping("/active-process")
    public ResponseEntity<String> getActiveProcessInstances(
            @RequestBody(required = false) Map<String, Object> variables) {
        String activeInstances = operateService.getActiveInstances(variables);
        return ResponseEntity.ok(activeInstances);
    }

    @PostMapping("/task/{taskId}/variables")
    public ResponseEntity<String> getTaskVariables(
        @PathVariable("taskId") String taskId,
        @RequestBody(required = false) Map<String, Object> body) {
        
        if (body == null) {
            return ResponseEntity.badRequest().body("Request body is required");
        }
        
        // Extract and validate input1 and input2
        String input1 = (String) body.get("input1");
        String input2 = (String) body.get("input2");
        
        if (input1 == null || input1.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("input1 is required and cannot be empty");
        }
        if (input2 == null || input2.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("input2 is required and cannot be empty");
        }
        
        // Log the variables for debugging
        System.out.println("TaskId: " + taskId);
        System.out.println("input1: " + input1);
        System.out.println("input2: " + input2);
        
        // Complete the task instead of just getting variables
        String result = operateService.completeTask(taskId, body);
        return ResponseEntity.ok(result);
    }
    
//    @PostMapping("/task/{taskId}/complete")
//    public ResponseEntity<String> completeTask(
//            @PathVariable("taskId") String taskId,
//            @RequestBody(required = false) Map<String, Object> variables) {
//        String result = operateService.completeTask(taskId, variables != null ? variables : Map.of());
//        return ResponseEntity.ok(result);
//    }
}
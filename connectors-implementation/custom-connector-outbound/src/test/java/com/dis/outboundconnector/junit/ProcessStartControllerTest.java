
package com.dis.outboundconnector.junit;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dis.outboundconnector.security.TestSecurityConfig;
//import com.dis.outboundconnector.TestSecurityConfig;
import com.dis.outboundconnector.service.CamundaOperateService;
import com.dis.outboundconnector.service.ProcessStartController;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep2;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
@Import(TestSecurityConfig.class) // Add this line

@WebMvcTest(ProcessStartController.class)
public class ProcessStartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ZeebeClient zeebeClient;
    @MockBean
    private CamundaOperateService operateService;

    @Test
    @DisplayName("Should start process instance successfully with variables")
    void shouldStartProcessInstance() throws Exception {
        // Sample request
        Map<String, Object> variables = Map.of("name", "Sharanya");
        String processId = "sample-process";
        String jsonBody = new ObjectMapper().writeValueAsString(variables);
        
        // Mock response
        ProcessInstanceEvent mockedEvent = mock(ProcessInstanceEvent.class);
        when(mockedEvent.getProcessInstanceKey()).thenReturn(12345L);
        when(mockedEvent.getBpmnProcessId()).thenReturn(processId);
        when(mockedEvent.getVersion()).thenReturn(1);
        
        // Mock Zeebe client behavior
        CreateProcessInstanceCommandStep1 step1 = mock(CreateProcessInstanceCommandStep1.class);
        CreateProcessInstanceCommandStep2 step2 = mock(CreateProcessInstanceCommandStep2.class);
        CreateProcessInstanceCommandStep3 step3 = mock(CreateProcessInstanceCommandStep3.class);
        
        when(zeebeClient.newCreateInstanceCommand()).thenReturn(step1);
        when(step1.bpmnProcessId(processId)).thenReturn(step2);
        when(step2.latestVersion()).thenReturn(step3);
        when(step3.variables(variables)).thenReturn(step3);
        
        ZeebeFuture<ProcessInstanceEvent> mockedFuture = mock(ZeebeFuture.class);
        when(mockedFuture.join()).thenReturn(mockedEvent);
        when(step3.send()).thenReturn(mockedFuture);
        
        // Perform POST call
        mockMvc.perform(post("/api/process/start")
                .param("processId", processId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processInstanceKey").value(12345))
                .andExpect(jsonPath("$.bpmnProcessId").value(processId))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    @DisplayName("Should start process instance without variables")
    void shouldStartProcessInstanceWithoutVariables() throws Exception {
        // Sample request without variables
        String processId = "sample-process";
        
        // Mock response
        ProcessInstanceEvent mockedEvent = mock(ProcessInstanceEvent.class);
        when(mockedEvent.getProcessInstanceKey()).thenReturn(12345L);
        when(mockedEvent.getBpmnProcessId()).thenReturn(processId);
        when(mockedEvent.getVersion()).thenReturn(1);
        
        // Mock Zeebe client behavior
        CreateProcessInstanceCommandStep1 step1 = mock(CreateProcessInstanceCommandStep1.class);
        CreateProcessInstanceCommandStep2 step2 = mock(CreateProcessInstanceCommandStep2.class);
        CreateProcessInstanceCommandStep3 step3 = mock(CreateProcessInstanceCommandStep3.class);
        
        when(zeebeClient.newCreateInstanceCommand()).thenReturn(step1);
        when(step1.bpmnProcessId(processId)).thenReturn(step2);
        when(step2.latestVersion()).thenReturn(step3);
        when(step3.variables(Map.of())).thenReturn(step3);
        
        ZeebeFuture<ProcessInstanceEvent> mockedFuture = mock(ZeebeFuture.class);
        when(mockedFuture.join()).thenReturn(mockedEvent);
        when(step3.send()).thenReturn(mockedFuture);
        
        // Perform POST call
        mockMvc.perform(post("/api/process/start")
                .param("processId", processId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processInstanceKey").value(12345))
                .andExpect(jsonPath("$.bpmnProcessId").value(processId))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    @DisplayName("Should return bad request when processId is missing")
    void shouldReturnBadRequestWhenProcessIdMissing() throws Exception {
        mockMvc.perform(post("/api/process/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get active process instances")
    void shouldGetActiveProcessInstances() throws Exception {
        // Given
        String expectedResponse = "Active instances response";
        Map<String, Object> variables = Map.of("status", "active");
        String jsonBody = new ObjectMapper().writeValueAsString(variables);
        
        when(operateService.getActiveInstances(variables)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/process/active-process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(operateService).getActiveInstances(variables);
    }

    @Test
    @DisplayName("Should complete task with valid inputs")
    void shouldCompleteTaskWithValidInputs() throws Exception {
        // Given
        String taskId = "task-123";
        String expectedResponse = "Task completed successfully";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input1", "value1");
        requestBody.put("input2", "value2");
        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        when(operateService.completeTask(taskId, requestBody)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/process/task/{taskId}/variables", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(operateService).completeTask(taskId, requestBody);
    }

    @Test
    @DisplayName("Should return bad request when request body is null for task completion")
    void shouldReturnBadRequestWhenRequestBodyIsNull() throws Exception {
        // Given
        String taskId = "task-123";

        // When & Then
        mockMvc.perform(post("/api/process/task/{taskId}/variables", taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Request body is required"));

        verify(operateService, never()).completeTask(anyString(), any());
    }

    @Test
    @DisplayName("Should return bad request when input1 is null")
    void shouldReturnBadRequestWhenInput1IsNull() throws Exception {
        // Given
        String taskId = "task-123";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input1", null);
        requestBody.put("input2", "value2");
        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        // When & Then
        mockMvc.perform(post("/api/process/task/{taskId}/variables", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("input1 is required and cannot be empty"));

        verify(operateService, never()).completeTask(anyString(), any());
    }

    @Test
    @DisplayName("Should return bad request when input1 is empty")
    void shouldReturnBadRequestWhenInput1IsEmpty() throws Exception {
        // Given
        String taskId = "task-123";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input1", "   ");
        requestBody.put("input2", "value2");
        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        // When & Then
        mockMvc.perform(post("/api/process/task/{taskId}/variables", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("input1 is required and cannot be empty"));

        verify(operateService, never()).completeTask(anyString(), any());
    }

    @Test
    @DisplayName("Should return bad request when input2 is null")
    void shouldReturnBadRequestWhenInput2IsNull() throws Exception {
        // Given
        String taskId = "task-123";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input1", "value1");
        requestBody.put("input2", null);
        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        // When & Then
        mockMvc.perform(post("/api/process/task/{taskId}/variables", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("input2 is required and cannot be empty"));

        verify(operateService, never()).completeTask(anyString(), any());
    }

    @Test
    @DisplayName("Should return bad request when input2 is empty")
    void shouldReturnBadRequestWhenInput2IsEmpty() throws Exception {
        // Given
        String taskId = "task-123";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input1", "value1");
        requestBody.put("input2", "");
        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        // When & Then
        mockMvc.perform(post("/api/process/task/{taskId}/variables", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("input2 is required and cannot be empty"));

        verify(operateService, never()).completeTask(anyString(), any());
    }

    @Test
    @DisplayName("Should handle task completion with additional variables")
    void shouldHandleTaskCompletionWithAdditionalVariables() throws Exception {
        // Given
        String taskId = "task-123";
        String expectedResponse = "Task completed with additional variables";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("input1", "value1");
        requestBody.put("input2", "value2");
        requestBody.put("additionalVar", "additionalValue");
        requestBody.put("numericVar", 123);
        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);

        when(operateService.completeTask(taskId, requestBody)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/process/task/{taskId}/variables", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(operateService).completeTask(taskId, requestBody);
    }

    @Test
    @DisplayName("Should handle process start with complex variables")
    void shouldHandleProcessStartWithComplexVariables() throws Exception {
        // Given
        Map<String, Object> complexVariables = new HashMap<>();
        complexVariables.put("name", "Sharanya");
        complexVariables.put("age", 25);
        complexVariables.put("isActive", true);
        complexVariables.put("tags", Map.of("department", "IT", "role", "developer"));
        
        String processId = "complex-process";
        String jsonBody = new ObjectMapper().writeValueAsString(complexVariables);
        
        // Mock response
        ProcessInstanceEvent mockedEvent = mock(ProcessInstanceEvent.class);
        when(mockedEvent.getProcessInstanceKey()).thenReturn(67890L);
        when(mockedEvent.getBpmnProcessId()).thenReturn(processId);
        when(mockedEvent.getVersion()).thenReturn(2);
        
        // Mock Zeebe client behavior
        CreateProcessInstanceCommandStep1 step1 = mock(CreateProcessInstanceCommandStep1.class);
        CreateProcessInstanceCommandStep2 step2 = mock(CreateProcessInstanceCommandStep2.class);
        CreateProcessInstanceCommandStep3 step3 = mock(CreateProcessInstanceCommandStep3.class);
        
        when(zeebeClient.newCreateInstanceCommand()).thenReturn(step1);
        when(step1.bpmnProcessId(processId)).thenReturn(step2);
        when(step2.latestVersion()).thenReturn(step3);
        when(step3.variables(complexVariables)).thenReturn(step3);
        
        ZeebeFuture<ProcessInstanceEvent> mockedFuture = mock(ZeebeFuture.class);
        when(mockedFuture.join()).thenReturn(mockedEvent);
        when(step3.send()).thenReturn(mockedFuture);
        
        // When & Then
        mockMvc.perform(post("/api/process/start")
                .param("processId", processId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processInstanceKey").value(67890))
                .andExpect(jsonPath("$.bpmnProcessId").value(processId))
                .andExpect(jsonPath("$.version").value(2));
    }
}

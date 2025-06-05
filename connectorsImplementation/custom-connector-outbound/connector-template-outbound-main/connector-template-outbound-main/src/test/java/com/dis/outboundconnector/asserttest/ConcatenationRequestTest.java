
package com.dis.outboundconnector.asserttest;

import static org.junit.jupiter.api.Assertions.*;

import com.dis.outboundconnector.dto.Authentication;
import com.dis.outboundconnector.dto.ConcatenationConnectorRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.error.ConnectorInputException;
import io.camunda.connector.test.outbound.OutboundConnectorContextBuilder;

import org.junit.jupiter.api.Test;

public class ConcatenationRequestTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldFailWhenValidate_NoInput1() throws JsonProcessingException {
        // given - input1 is null, input2 has value
        ConcatenationConnectorRequest input = new ConcatenationConnectorRequest(null, "valid_input2");
        var context = OutboundConnectorContextBuilder
                .create()
                .variables(input).build();

        // when & then
        ConnectorInputException exception = assertThrows(ConnectorInputException.class, () -> {
            context.bindVariables(ConcatenationConnectorRequest.class);
        });
        
        assertTrue(exception.getMessage().contains("input1"), 
                "Message should contain 'input1'");
    }

    @Test
    public void shouldFailWhenValidate_NoInput2() throws JsonProcessingException {
        // given - input1 has value, input2 is null
        ConcatenationConnectorRequest input = new ConcatenationConnectorRequest("valid_input1", null);
        var context = OutboundConnectorContextBuilder
                .create()
                .variables(input).build();

        // when & then
        ConnectorInputException exception = assertThrows(ConnectorInputException.class, () -> {
            context.bindVariables(ConcatenationConnectorRequest.class);
        });
        
        assertTrue(exception.getMessage().contains("input2"), 
                "Message should contain 'input2'");
    }
}
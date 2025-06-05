
package com.dis.outboundconnector.asserttest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.dis.outboundconnector.ConcatenationConnectorFunction;
import com.dis.outboundconnector.dto.Authentication;
import com.dis.outboundconnector.dto.ConcatenationConnectorRequest;
import com.dis.outboundconnector.dto.ConcatenationConnectorResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.test.outbound.OutboundConnectorContextBuilder;

public class ConcatenationFunctionTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldReturnExpectedResultWhenExecute() throws Exception {
        // given
        ConcatenationConnectorFunction function = new ConcatenationConnectorFunction();
        var context = OutboundConnectorContextBuilder.create()
                .variable("input1", "value1")
                .variable("input2", "value2")
                .build();

        // when
        Object result = function.execute(context);

        // then
        assertNotNull(result);
        assertTrue(result instanceof ConcatenationConnectorResult);

        ConcatenationConnectorResult concatenationResult = (ConcatenationConnectorResult) result;
        assertEquals("value1 value2", concatenationResult.concatenationResult());
    }
}
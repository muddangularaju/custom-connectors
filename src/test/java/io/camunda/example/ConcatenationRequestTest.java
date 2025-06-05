package io.camunda.example;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.connector.api.error.ConnectorInputException;
import io.camunda.connector.test.outbound.OutboundConnectorContextBuilder;
import io.camunda.example.dto.ConcatenationConnectorRequest;

public class ConcatenationRequestTest {

	String input1, input2;

	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void shouldFailWhenValidate_NoInput1() throws JsonProcessingException {
		// given
		var input = new ConcatenationConnectorRequest(input1, input2);

		var context = OutboundConnectorContextBuilder.create().variables(input).build();
		// when
		assertThatThrownBy(() -> context.bindVariables(ConcatenationConnectorRequest.class))
				// then
				.isInstanceOf(ConnectorInputException.class).hasMessageContaining("input1");
	}

	@Test
	void shouldFailWhenValidate_NoInput2() throws JsonProcessingException {
		// given
		var input = new ConcatenationConnectorRequest(input1, input2);

		var context = OutboundConnectorContextBuilder.create().variables(input).build();
		// when
		assertThatThrownBy(() -> context.bindVariables(ConcatenationConnectorRequest.class))
				// then
				.isInstanceOf(ConnectorInputException.class).hasMessageContaining("input2");
	}

}
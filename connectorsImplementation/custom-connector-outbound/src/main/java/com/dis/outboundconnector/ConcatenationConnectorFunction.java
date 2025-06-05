package com.dis.outboundconnector;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dis.outboundconnector.dto.ConcatenationConnectorRequest;
import com.dis.outboundconnector.dto.ConcatenationConnectorResult;

@OutboundConnector(
		name = "concatenation-connector",
	    inputVariables = {"input1", "input2"},
	    type = "io.camunda:concatenation-api:1")

public class ConcatenationConnectorFunction implements OutboundConnectorFunction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcatenationConnectorFunction.class);

  @Override
  public Object execute(OutboundConnectorContext context) {
    final var connectorRequest = context.bindVariables(ConcatenationConnectorRequest.class);
    return executeConnector(connectorRequest);
  }

  private ConcatenationConnectorResult executeConnector(final ConcatenationConnectorRequest connectorRequest) {
	  LOGGER.info("Executing my connector with request {}", connectorRequest);
	  String concatenationResult = connectorRequest.input1() + " " +connectorRequest.input2();
	  var result = new ConcatenationConnectorResult(concatenationResult); 

	  LOGGER.info("Connector executed with result {}:", result);
	  return result;
	  }

}

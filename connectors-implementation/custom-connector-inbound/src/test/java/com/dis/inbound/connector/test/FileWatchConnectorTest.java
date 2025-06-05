package com.dis.inbound.connector.test;

import static org.assertj.core.api.Assertions.assertThat;
import io.camunda.connector.test.inbound.InboundConnectorContextBuilder;

import org.junit.jupiter.api.Test;

import com.dis.inbound.connector.ConnectorProperties;

public class FileWatchConnectorTest {

    String eventToMonitor = "ENTRY_CREATE";
    String directory = "C:\\Users\\Camunda\\Downloads";
    String pollingInterval = "30";

    @Test
    void shouldFailWhenValidate_NoEventToMonitor() {
    // given
    var input = new ConnectorProperties(eventToMonitor, directory, pollingInterval);    
    var context = InboundConnectorContextBuilder.create().properties(input).build();

    // when
    var connectorInput = context.bindProperties(ConnectorProperties.class);

    // then    
    assertThat(connectorInput)
        .isInstanceOf(ConnectorProperties.class)
        .extracting("eventToMonitor")
        .isEqualTo("ENTRY_CREATE");
    }

    @Test
    void shouldFailWhenValidate_NoDirectory() {
    // given    
    var input = new ConnectorProperties(eventToMonitor, directory, pollingInterval);
    var context = InboundConnectorContextBuilder.create().properties(input).build();

    // when
    var connectorInput = context.bindProperties(ConnectorProperties.class);

    // then    
    assertThat(connectorInput)
        .isInstanceOf(ConnectorProperties.class)
        .extracting("directory")
        .isEqualTo("C:\\Users\\Camunda\\Downloads");
    }
    
    @Test
    void shouldFailWhenValidate_NoPollingInterval() {
    // given   
    var input = new ConnectorProperties(eventToMonitor, directory, pollingInterval);
    var context = InboundConnectorContextBuilder.create().properties(input).build();

    // when
    var connectorInput = context.bindProperties(ConnectorProperties.class);

    // then    
    assertThat(connectorInput)
        .isInstanceOf(ConnectorProperties.class)
        .extracting("pollingInterval")
        .isEqualTo("30");
    }
}

package com.dis.inbound.connector;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dis.inbound.connector.subscription.WatchServiceSubscription;
import com.dis.inbound.connector.subscription.WatchServiceSubscriptionEvent;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.CorrelationRequest;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.InboundConnectorExecutable;

@InboundConnector(
        name = "Watch Service Inbound Connector", 
        type = "io.camunda:watchserviceinbound:1"
    )  
public class ConnectorExecutable implements InboundConnectorExecutable<InboundConnectorContext> {

  private WatchServiceSubscription subscription;
  private InboundConnectorContext context;
  private ExecutorService executorService;
  public CompletableFuture<?> future;

  @Override
  public void activate(InboundConnectorContext context) {
    ConnectorProperties props = context.bindProperties(ConnectorProperties.class);
    this.context = context;
    this.executorService = Executors.newSingleThreadExecutor();

    this.future =
      CompletableFuture.runAsync(
        () -> {
          this.subscription = new WatchServiceSubscription(
            props.eventToMonitor(), 
            props.directory(), 
            props.pollingInterval(), 
            this::onEvent);
        },
        this.executorService);
  }

  private void onEvent(WatchServiceSubscriptionEvent rawEvent) {
    context.correlate(CorrelationRequest.builder().variables(new ConnectorEvent(rawEvent)).build());
  }

  @Override
  public void deactivate() {
    subscription.stop();
  }
}

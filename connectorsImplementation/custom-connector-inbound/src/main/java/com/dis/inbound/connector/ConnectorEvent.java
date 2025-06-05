package com.dis.inbound.connector;

import com.dis.inbound.connector.subscription.WatchServiceSubscriptionEvent;

/**
 * Data model of an event produced by the inbound Connector
 *
 * @param event
 */
//sending event ( output) to the process instance 
public record ConnectorEvent(WatchServiceSubscriptionEvent event) {}

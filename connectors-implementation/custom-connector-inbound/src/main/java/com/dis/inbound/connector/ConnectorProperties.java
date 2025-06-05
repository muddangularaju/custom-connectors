package com.dis.inbound.connector;

public record ConnectorProperties(
    String eventToMonitor,
    String directory,
    String pollingInterval
) {}

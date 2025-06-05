package com.dis.inbound.connector.subscription;

public record WatchServiceSubscriptionEvent(String monitoredEvent, String directory, String fileName){}

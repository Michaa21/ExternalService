package com.example.externalservice.outbox;

public enum OutboxEventStatus {
    NEW,
    PUBLISHED,
    FAILED
}
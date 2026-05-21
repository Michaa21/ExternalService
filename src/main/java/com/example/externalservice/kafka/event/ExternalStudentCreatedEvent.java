package com.example.externalservice.kafka.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ExternalStudentCreatedEvent(
        UUID eventId,
        UUID originalEventId,
        UUID studentId,
        String extraInfo,
        String email,
        Integer age,
        OffsetDateTime createdAt
) {
}

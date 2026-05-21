package com.example.externalservice.kafka.event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record StudentCreateRequestedEvent(
        UUID eventId,
        UUID studentId,
        String name,
        String email,
        Integer age,
        List<String> lessonTitles,
        OffsetDateTime createdAt
) {
}

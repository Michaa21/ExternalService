package com.example.externalservice.kafka.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DlqEvent(
        UUID evetId,
        String sourceTopic,
        String originalPayload,
        String errorMessage,
        OffsetDateTime failedAt
) {
}

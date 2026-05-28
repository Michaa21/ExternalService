package com.example.externalservice.kafka;

import com.example.externalservice.dto.StudentCreateRequestedEvent;
import com.example.externalservice.service.StudentCreateRequestedEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentCreateRequestedConsumer {

    private final ObjectMapper objectMapper;
    private final StudentCreateRequestedEventHandler eventHandler;
    private final DlqPublisher dlqPublisher;

    @KafkaListener(
            topics = KafkaTopics.STUDENT_CREATE_REQUESTS,
            groupId = "external-service"
    )
    public void consume(String payload, Acknowledgment acknowledgment) {
        String originalKey = null;

        try {
            StudentCreateRequestedEvent event =
                    objectMapper.readValue(payload, StudentCreateRequestedEvent.class);

            originalKey = event.eventId().toString();

            eventHandler.handle(event, acknowledgment);
        } catch (Exception exception) {
            log.error("Failed to process student create requested event, sending to DLQ. Payload: {}",
                    payload,
                    exception
            );

            String dlqKey = originalKey != null ? originalKey : "unknown";

            dlqPublisher.publish(
                    KafkaTopics.STUDENT_CREATE_REQUESTS,
                    dlqKey,
                    payload,
                    exception
            );

            acknowledgment.acknowledge();
        }
    }
}
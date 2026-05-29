package com.example.externalservice.kafka;

import com.example.externalservice.dto.StudentCreateRequestedEvent;
import com.example.externalservice.service.StudentCreateRequestedEventHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        StudentCreateRequestedEvent event;

        try {
            event = objectMapper.readValue(payload, StudentCreateRequestedEvent.class);
        } catch (JsonProcessingException exception) {
            log.error("Failed to deserialize student create requested event, sending to DLQ. Payload: {}",
                    payload,
                    exception
            );

            dlqPublisher.publish(
                    KafkaTopics.STUDENT_CREATE_REQUESTS,
                    "unknown",
                    payload,
                    exception
            );

            acknowledgment.acknowledge();
            return;
        }
        eventHandler.handle(event, acknowledgment);
    }
}

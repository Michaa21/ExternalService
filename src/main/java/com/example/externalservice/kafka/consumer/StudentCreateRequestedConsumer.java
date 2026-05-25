package com.example.externalservice.kafka.consumer;

import com.example.externalservice.kafka.KafkaTopics;
import com.example.externalservice.kafka.event.StudentCreateRequestedEvent;
import com.example.externalservice.kafka.service.DlqPublisher;
import com.example.externalservice.kafka.service.StudentCreateRequestedEventHandler;
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
        try {
            StudentCreateRequestedEvent event =
                    objectMapper.readValue(payload, StudentCreateRequestedEvent.class);

            eventHandler.handle(event);

            acknowledgment.acknowledge();
        } catch (Exception exception) {
            log.error("Failed to process student create requested event, sending to DLQ. Payload: {}",
                    payload,
                    exception
            );

            dlqPublisher.publish(
                    KafkaTopics.STUDENT_CREATE_REQUESTS,
                    payload,
                    exception
            );

            acknowledgment.acknowledge();
        }
    }
}
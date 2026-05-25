package com.example.externalservice.kafka.service;

import com.example.externalservice.kafka.KafkaTopics;
import com.example.externalservice.kafka.event.DlqEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DlqPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String sourceTopic, String originalPayload, Exception exception) {
        try {
            DlqEvent dlqEvent = new DlqEvent(
                    UUID.randomUUID(),
                    sourceTopic,
                    originalPayload,
                    exception.getClass().getSimpleName() + ": " + exception.getMessage(),
                    OffsetDateTime.now()
            );

            String payload = objectMapper.writeValueAsString(dlqEvent);

            kafkaTemplate.send(
                    KafkaTopics.DLQ_EVENTS,
                    dlqEvent.eventId().toString(),
                    payload
            ).get();

            log.info(
                    "DLQ event published to topic {} with key {}",
                    KafkaTopics.DLQ_EVENTS,
                    dlqEvent.eventId()
            );
        } catch (JsonProcessingException exceptionToSerializeDlq) {
            throw new IllegalStateException("Failed to serialize DLQ event", exceptionToSerializeDlq);
        } catch (Exception exceptionToPublishDlq) {
            throw new IllegalStateException("Failed to publish DLQ event", exceptionToPublishDlq);
        }
    }
}
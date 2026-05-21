package com.example.externalservice.kafka.consumer;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.kafka.KafkaTopics;
import com.example.externalservice.kafka.event.ExternalStudentCreatedEvent;
import com.example.externalservice.kafka.event.StudentCreateRequestedEvent;
import com.example.externalservice.kafka.processed.ProcessedEvent;
import com.example.externalservice.kafka.processed.ProcessedEventRepository;
import com.example.externalservice.outbox.OutboxEventService;
import com.example.externalservice.repository.ExternalStudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentCreateRequestedConsumer {

    private final ObjectMapper objectMapper;
    private final ExternalStudentRepository externalStudentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final OutboxEventService outboxEventService;

    @KafkaListener(
            topics = KafkaTopics.STUDENT_CREATE_REQUESTS,
            groupId = "external-service"
    )
    @Transactional
    public void consume(String payload, Acknowledgment acknowledgment) {
        try {
            StudentCreateRequestedEvent event =
                    objectMapper.readValue(payload, StudentCreateRequestedEvent.class);

            if (processedEventRepository.existsByEventId(event.eventId())) {
                log.info("Event {} already processed, skipping", event.eventId());
                acknowledgment.acknowledge();
                return;
            }

            ExternalStudent externalStudent = externalStudentRepository.findByStudentId(event.studentId())
                    .orElseGet(ExternalStudent::new);

            externalStudent.setStudentId(event.studentId());
            externalStudent.setName(event.name());
            externalStudent.setEmail(event.email());
            externalStudent.setAge(event.age());
            externalStudent.setExtraInfo("extra-info-for-" + event.name());

            ExternalStudent savedExternalStudent = externalStudentRepository.save(externalStudent);

            UUID responseEventId = UUID.randomUUID();

            ExternalStudentCreatedEvent responseEvent = new ExternalStudentCreatedEvent(
                    responseEventId,
                    event.eventId(),
                    savedExternalStudent.getStudentId(),
                    savedExternalStudent.getExtraInfo(),
                    savedExternalStudent.getEmail(),
                    savedExternalStudent.getAge(),
                    OffsetDateTime.now()
            );

            outboxEventService.createOutboxEvent(
                    responseEventId,
                    "EXTERNAL_STUDENT",
                    savedExternalStudent.getStudentId(),
                    "EXTERNAL_STUDENT_CREATED",
                    KafkaTopics.EXTERNAL_STUDENT_CREATED_EVENTS,
                    responseEvent
            );

            ProcessedEvent processedEvent = new ProcessedEvent();
            processedEvent.setId(UUID.randomUUID());
            processedEvent.setEventId(event.eventId());
            processedEvent.setEventType("STUDENT_CREATE_REQUESTED");

            processedEventRepository.save(processedEvent);

            acknowledgment.acknowledge();

            log.info("Student create requested event {} processed successfully", event.eventId());
        } catch (Exception exception) {
            log.error("Failed to process student create requested event", exception);
            throw new IllegalStateException("Failed to process student create requested event", exception);
        }
    }
}
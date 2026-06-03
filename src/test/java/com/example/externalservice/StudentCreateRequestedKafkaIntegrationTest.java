package com.example.externalservice;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.StudentCreateRequestedEvent;
import com.example.externalservice.kafka.KafkaTopics;
import com.example.externalservice.repository.ExternalStudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class StudentCreateRequestedKafkaIntegrationTest extends IntegrationTestBase {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExternalStudentRepository externalStudentRepository;

    @Test
    void shouldCreateExternalStudent_whenStudentCreateRequestedEventReceived() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        StudentCreateRequestedEvent event = new StudentCreateRequestedEvent(
                eventId,
                studentId,
                "Bob",
                "bob@mail.com",
                18,
                List.of("Java", "Spring"),
                OffsetDateTime.now()
        );

        String payload = objectMapper.writeValueAsString(event);

        kafkaTemplate.send(
                KafkaTopics.STUDENT_CREATE_REQUESTS,
                eventId.toString(),
                payload
        ).get();

        await()
                .pollDelay(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    ExternalStudent saved = externalStudentRepository.findByStudentId(studentId)
                            .orElseThrow();

                    assertThat(saved.getStudentId()).isEqualTo(studentId);
                    assertThat(saved.getName()).isEqualTo("Bob");
                    assertThat(saved.getEmail()).isEqualTo("bob@mail.com");
                    assertThat(saved.getAge()).isEqualTo(18);
                    assertThat(saved.getExtraInfo()).isEqualTo("extra-info-for-Bob");
                });
    }
}
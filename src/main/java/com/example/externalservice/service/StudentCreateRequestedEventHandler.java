package com.example.externalservice.service;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.StudentCreateRequestedEvent;
import com.example.externalservice.repository.ExternalStudentRepository;
import com.example.externalservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentCreateRequestedEventHandler {

    private static final String EVENT_TYPE = "STUDENT_CREATE_REQUESTED";

    private final ExternalStudentRepository externalStudentRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void handle(StudentCreateRequestedEvent event, Acknowledgment acknowledgment) {

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                acknowledgment.acknowledge();
            }
        });

        int inserted = processedEventRepository.insertIfNotExists(
                UUID.randomUUID(),
                event.eventId(),
                EVENT_TYPE
        );

        if (inserted == 0) {
            log.info("Event {} already processed, skipping", event.eventId());
            return;
        }

        ExternalStudent externalStudent = externalStudentRepository.findByStudentId(event.studentId())
                .orElseGet(ExternalStudent::new);

        externalStudent.setStudentId(event.studentId());
        externalStudent.setName(event.name());
        externalStudent.setEmail(event.email());
        externalStudent.setAge(event.age());
        externalStudent.setExtraInfo("extra-info-for-" + event.name());

        externalStudentRepository.save(externalStudent);

        log.info("Student create requested event {} processed successfully", event.eventId());
    }
}

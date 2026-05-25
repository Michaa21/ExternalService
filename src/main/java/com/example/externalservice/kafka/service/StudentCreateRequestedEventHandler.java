package com.example.externalservice.kafka.service;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.kafka.event.StudentCreateRequestedEvent;
import com.example.externalservice.kafka.processed.ProcessedEvent;
import com.example.externalservice.kafka.processed.ProcessedEventRepository;
import com.example.externalservice.repository.ExternalStudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentCreateRequestedEventHandler {

    private final ExternalStudentRepository externalStudentRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void handle(StudentCreateRequestedEvent event) {
        if (processedEventRepository.existsByEventId(event.eventId())) {
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

        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setId(UUID.randomUUID());
        processedEvent.setEventId(event.eventId());
        processedEvent.setEventType("STUDENT_CREATE_REQUESTED");

        processedEventRepository.save(processedEvent);

        log.info("Student create requested event {} processed successfully", event.eventId());
    }
}
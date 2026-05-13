package com.example.externalservice.service;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.mapper.ExternalStudentMapper;
import com.example.externalservice.repository.ExternalStudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExternalStudentService {

    private final ExternalStudentRepository externalStudentRepository;
    private final ExternalStudentMapper externalStudentMapper;

    @Transactional(readOnly = true)
    public ExternalStudentResponse getByStudentId(UUID studentId) {
        ExternalStudent externalStudent = externalStudentRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    log.warn("External student not found by studentId: {}", studentId);

                    return new EntityNotFoundException(
                            "External student not found by studentId: " + studentId);
                });

        return externalStudentMapper.toResponse(externalStudent);
    }

    @Transactional
    public ExternalStudentResponse create(ExternalStudentRequest request) {
        return externalStudentRepository.findByStudentId(request.getStudentId())
                .map(existingStudent -> {
                    log.info(
                            "External student with studentId {} already exists",
                            existingStudent.getStudentId()
                    );

                    return externalStudentMapper.toResponse(existingStudent);
                })
                .orElseGet(() -> createNewExternalStudent(request));
    }

    @Transactional
    public void compensateStudentCreation(UUID studentId) {
        externalStudentRepository.findByStudentId(studentId)
                .ifPresentOrElse(
                        externalStudent -> {
                            externalStudentRepository.delete(externalStudent);
                            log.info("External student with studentId {} deleted as saga compensation",
                                    studentId);
                        },
                        () -> log.info(
                                "External student with studentId {} already absent, compensation skipped",
                                studentId
                        )
                );
    }

    private ExternalStudentResponse createNewExternalStudent(ExternalStudentRequest request) {
        ExternalStudent externalStudent = externalStudentMapper.toEntity(request);
        ExternalStudent saved = externalStudentRepository.save(externalStudent);

        log.info("External student with studentId {} created", saved.getStudentId());

        return externalStudentMapper.toResponse(saved);
    }
}
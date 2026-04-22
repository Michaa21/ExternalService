package com.example.externalservice.service;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.repository.ExternalStudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ExternalStudentService {

    private final ExternalStudentRepository externalStudentRepository;

    @Transactional(readOnly = true)
    public ExternalStudentResponse getById(UUID id) {
        ExternalStudent externalStudent = externalStudentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("External student not found: " + id));

        return new ExternalStudentResponse(externalStudent.getId(),
                externalStudent.getExtraInfo());
    }

    @Transactional
    public ExternalStudentResponse create(ExternalStudentRequest request) {
        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setExtraInfo(request.getExtraInfo());

        ExternalStudent saved = externalStudentRepository.save(externalStudent);

        return new ExternalStudentResponse(saved.getId(),
                saved.getExtraInfo());
    }

    @Transactional
    public void deleteById(UUID id) {
        externalStudentRepository.deleteById(id);
    }
}
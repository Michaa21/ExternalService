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
    public ExternalStudentResponse getByStudentId(UUID studentId) {
        ExternalStudent externalStudent = externalStudentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new EntityNotFoundException("External student not found by studentId: " + studentId));

        return new ExternalStudentResponse(
                externalStudent.getStudentId(),
                externalStudent.getExtraInfo()
        );
    }

    @Transactional
    public ExternalStudentResponse create(ExternalStudentRequest request) {
        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setStudentId(request.getStudentId());
        externalStudent.setExtraInfo("extra-info-for-" + request.getName());

        ExternalStudent saved = externalStudentRepository.save(externalStudent);

        return new ExternalStudentResponse(saved.getStudentId(), saved.getExtraInfo());
    }

    @Transactional
    public void deleteById(UUID id) {
        externalStudentRepository.deleteById(id);
    }
}
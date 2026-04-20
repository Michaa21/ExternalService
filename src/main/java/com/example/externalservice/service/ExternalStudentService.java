package com.example.externalservice.service;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.repository.ExternalStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ExternalStudentService {

    private final ExternalStudentRepository externalStudentRepository;

    public ExternalStudentResponse getById(UUID id) {
        ExternalStudent externalStudent = externalStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("External student not found"));

        return new ExternalStudentResponse(externalStudent.getId(),
                externalStudent.getExtraInfo());
    }

    public ExternalStudentResponse create(ExternalStudentRequest request) {
        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setExtraInfo(request.getExtraInfo());

        ExternalStudent saved = externalStudentRepository.save(externalStudent);

        return new ExternalStudentResponse(saved.getId(),
                saved.getExtraInfo());
    }

    public void deleteById(UUID id){
        externalStudentRepository.deleteById(id);
    }
}
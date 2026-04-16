package com.example.externalservice.service;

import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import org.springframework.stereotype.Service;

@Service
public class ExternalStudentService {

    public ExternalStudentResponse getById(String id) {
        return new ExternalStudentResponse("extra-info-for-" + id);
    }

    public ExternalStudentResponse create(ExternalStudentRequest request) {
        return new ExternalStudentResponse(request.getExtraInfo());
    }
}
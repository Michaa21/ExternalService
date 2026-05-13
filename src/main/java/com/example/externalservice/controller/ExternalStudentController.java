package com.example.externalservice.controller;

import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.service.ExternalStudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/external/students")
@RequiredArgsConstructor
@Validated
public class ExternalStudentController {

    private final ExternalStudentService externalStudentService;

    @PostMapping
    public ResponseEntity<ExternalStudentResponse> create(@Valid @RequestBody ExternalStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(externalStudentService.create(request));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<ExternalStudentResponse> getExtra(@PathVariable @NotNull UUID studentId) {
        return ResponseEntity.ok(externalStudentService.getByStudentId(studentId));
    }

    @DeleteMapping("/{studentId}/compensation")
    public ResponseEntity<Void> compensateStudentCreation(@PathVariable @NotNull UUID studentId) {
        externalStudentService.compensateStudentCreation(studentId);
        return ResponseEntity.noContent().build();
    }

}
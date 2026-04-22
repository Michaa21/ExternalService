package com.example.externalservice.controller;

import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.service.ExternalStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/external/students")
@RequiredArgsConstructor
public class ExternalStudentController {

    private final ExternalStudentService externalStudentService;

    @PostMapping
    public ResponseEntity<ExternalStudentResponse> create(@RequestBody ExternalStudentRequest request) {
        return ResponseEntity.ok(externalStudentService.create(request));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<ExternalStudentResponse> getExtra(@PathVariable UUID studentId) {
        return ResponseEntity.ok(externalStudentService.getByStudentId(studentId));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID studentId) {
        externalStudentService.deleteByStudentId(studentId);
        return ResponseEntity.noContent().build();
    }

}
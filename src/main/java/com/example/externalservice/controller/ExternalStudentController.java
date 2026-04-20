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

    @GetMapping("/{id}")
    public ResponseEntity<ExternalStudentResponse> getExtra(@PathVariable UUID id) {
        return ResponseEntity.ok(externalStudentService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        externalStudentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
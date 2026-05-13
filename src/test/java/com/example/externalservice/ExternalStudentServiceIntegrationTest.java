package com.example.externalservice;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.repository.ExternalStudentRepository;
import com.example.externalservice.service.ExternalStudentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExternalStudentServiceIntegrationTest extends ExternalServiceApplicationTests {

    @Autowired
    ExternalStudentService externalStudentService;

    @Autowired
    ExternalStudentRepository externalStudentRepository;

    @BeforeEach
    void setUp() {
        externalStudentRepository.deleteAll();
    }

    @Test
    void create_shouldSaveExternalStudent() {
        UUID studentId = UUID.randomUUID();

        ExternalStudentRequest request = new ExternalStudentRequest(
                studentId,
                "Bob",
                "bob@mail.com",
                18
        );

        ExternalStudentResponse result = externalStudentService.create(request);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals("extra-info-for-Bob", result.getExtraInfo());

        ExternalStudent saved = externalStudentRepository.findByStudentId(studentId)
                .orElseThrow();

        assertEquals(studentId, saved.getStudentId());
        assertEquals("extra-info-for-Bob", saved.getExtraInfo());
    }

    @Test
    void create_shouldReturnExistingStudent_whenStudentAlreadyExists() {
        UUID studentId = UUID.randomUUID();

        ExternalStudent existingStudent = new ExternalStudent();
        existingStudent.setStudentId(studentId);
        existingStudent.setName("Bob");
        existingStudent.setEmail("bob@mail.com");
        existingStudent.setAge(18);
        existingStudent.setExtraInfo("extra-info-for-Bob");

        externalStudentRepository.save(existingStudent);

        ExternalStudentRequest request = new ExternalStudentRequest(
                studentId,
                "Alex",
                "alex@mail.com",
                25
        );

        ExternalStudentResponse result = externalStudentService.create(request);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals("Bob", result.getName());
        assertEquals("bob@mail.com", result.getEmail());
        assertEquals(18, result.getAge());
        assertEquals("extra-info-for-Bob", result.getExtraInfo());

        assertEquals(1, externalStudentRepository.count());
    }

    @Test
    void getByStudentId_shouldReturnExternalStudent() {
        UUID studentId = UUID.randomUUID();

        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setStudentId(studentId);
        externalStudent.setExtraInfo("extra-info-for-Bob");
        externalStudent.setName("Bob");
        externalStudent.setEmail("bob@mail.com");
        externalStudent.setAge(18);

        externalStudentRepository.save(externalStudent);

        ExternalStudentResponse result = externalStudentService.getByStudentId(studentId);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals("extra-info-for-Bob", result.getExtraInfo());
    }

    @Test
    void getByStudentId_shouldThrowException_whenStudentNotFound() {
        UUID studentId = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class,
                () -> externalStudentService.getByStudentId(studentId));
    }

    @Test
    void compensateStudentCreation_shouldDeleteExternalStudent() {
        UUID studentId = UUID.randomUUID();

        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setStudentId(studentId);
        externalStudent.setExtraInfo("extra-info-for-Bob");
        externalStudent.setName("Bob");
        externalStudent.setEmail("bob@mail.com");
        externalStudent.setAge(18);

        externalStudentRepository.save(externalStudent);

        externalStudentService.compensateStudentCreation(studentId);

        assertTrue(externalStudentRepository.findByStudentId(studentId).isEmpty());
    }

    @Test
    void compensateStudentCreation_shouldDoNothing_whenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();

        externalStudentService.compensateStudentCreation(studentId);

        assertTrue(externalStudentRepository.findByStudentId(studentId).isEmpty());
    }
}
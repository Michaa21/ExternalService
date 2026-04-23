package com.example.externalservice.controller;

import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.service.ExternalStudentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalStudentController.class)
class ExternalStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExternalStudentService externalStudentService;

    @Test
    void create_shouldReturn200() throws Exception {
        UUID studentId = UUID.randomUUID();

        ExternalStudentResponse response =
                new ExternalStudentResponse(studentId, "extra-info-for-Bob");

        when(externalStudentService.create(org.mockito.ArgumentMatchers.any()))
                .thenReturn(response);

        mockMvc.perform(post("/external/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "%s",
                                  "name": "Bob"
                                }
                                """.formatted(studentId)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.studentId").value(studentId.toString()))
                .andExpect(jsonPath("$.extraInfo").value("extra-info-for-Bob"));
    }

    @Test
    void getExtra_shouldReturn200() throws Exception {
        UUID studentId = UUID.randomUUID();

        ExternalStudentResponse response =
                new ExternalStudentResponse(studentId, "extra-info-for-Bob");

        when(externalStudentService.getByStudentId(studentId)).thenReturn(response);

        mockMvc.perform(get("/external/students/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.studentId").value(studentId.toString()))
                .andExpect(jsonPath("$.extraInfo").value("extra-info-for-Bob"));
    }

    @Test
    void getExtra_shouldReturn404_whenStudentNotFound() throws Exception {
        UUID studentId = UUID.randomUUID();

        when(externalStudentService.getByStudentId(studentId))
                .thenThrow(new EntityNotFoundException("External student not found by studentId: " + studentId));

        mockMvc.perform(get("/external/students/{studentId}", studentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        UUID studentId = UUID.randomUUID();

        doNothing().when(externalStudentService).deleteByStudentId(studentId);

        mockMvc.perform(delete("/external/students/{studentId}", studentId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
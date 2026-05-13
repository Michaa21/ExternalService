package com.example.externalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalStudentResponse {

    private UUID studentId;
    private String name;
    private String email;
    private Integer age;
    private String extraInfo;
}
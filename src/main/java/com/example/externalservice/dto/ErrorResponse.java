package com.example.externalservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ErrorResponse {
    private Integer status;
    private String message;
    private OffsetDateTime timestamp;
}

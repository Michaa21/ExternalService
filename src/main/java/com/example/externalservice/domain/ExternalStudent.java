package com.example.externalservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class ExternalStudent {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID studentId;

    private String extraInfo;
}
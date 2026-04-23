package com.example.externalservice.repository;

import com.example.externalservice.domain.ExternalStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExternalStudentRepository extends JpaRepository<ExternalStudent, UUID> {

    Optional<ExternalStudent> findByStudentId(UUID studentId);

    void deleteByStudentId(UUID studentId);
}

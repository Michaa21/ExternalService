package com.example.externalservice.mapper;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExternalStudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "extraInfo", expression = "java(\"extra-info-for-\" + request.getName())")
    ExternalStudent toEntity(ExternalStudentRequest request);

    ExternalStudentResponse toResponse(ExternalStudent externalStudent);
}
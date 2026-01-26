package org.example.cinecore.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude
public record GenericResponse<T>(
        Boolean status,
        String message,
        T payload
) {
}

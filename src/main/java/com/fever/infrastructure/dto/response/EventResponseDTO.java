package com.fever.infrastructure.dto.response;

import com.fever.domain.model.Error;

import java.util.List;

public record EventResponseDTO(DataDTO dataDTO, List<Error> error) {
}

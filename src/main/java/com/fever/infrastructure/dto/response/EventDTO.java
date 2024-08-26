package com.fever.infrastructure.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventDTO(
        String id,
        String title,
        LocalDate start_date,
        LocalTime start_time,
        LocalDate end_date,
        LocalTime end_time,
        BigDecimal min_price,
        BigDecimal max_price
) {
}

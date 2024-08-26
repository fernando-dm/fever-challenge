package com.fever.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record Event(
        String eventId,
        LocalDateTime eventStartDate,
        LocalDateTime eventEndDate,
        LocalDateTime sellFrom,
        LocalDateTime sellTo,
        boolean soldOut,
        List<Zone> zones,
        Error error
) {}

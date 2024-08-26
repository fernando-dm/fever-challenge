package com.fever.domain.model;

import java.math.BigDecimal;

public record Zone(
        String zoneId,
        int capacity,
        BigDecimal price,
        String name,
        boolean numbered
) {}

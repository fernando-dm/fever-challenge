package com.fever.domain.model;

import java.util.List;

public record EventList(
        String baseEventId,
        String sellMode,
        String title,
        List<Event> events
) {}

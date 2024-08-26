package com.fever.application.usecases;

import com.fever.domain.repositories.FindEvents;
import com.fever.infrastructure.dto.response.EventResponseDTO;
import com.fever.application.usecases.eventMapper.EventMapper;
import com.fever.domain.model.EventList;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FindEventsUseCase {
    private final FindEvents findEvents;

    public FindEventsUseCase(FindEvents findEvents) {
        this.findEvents = findEvents;
    }

    public EventResponseDTO getEventsBetweenDates(LocalDate startDate, LocalDate endDate) {

        validate(startDate, endDate);

        List<EventList> eventsBetweenDates = findEvents.getEventsBetweenDates(startDate, endDate);

        return EventMapper.mapToEventResponseDTO(eventsBetweenDates);
    }

    private void validate(LocalDate startDate, LocalDate endDate) {
        if ((startDate == null || endDate == null || startDate.isAfter(endDate))) {
            try {
                throw new Exception("Invalid dates/time format");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

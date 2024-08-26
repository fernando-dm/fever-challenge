package com.fever.application.usecases.eventMapper;

import com.fever.domain.model.Error;
import com.fever.domain.model.Event;
import com.fever.domain.model.EventList;
import com.fever.domain.model.Zone;
import com.fever.infrastructure.dto.response.DataDTO;
import com.fever.infrastructure.dto.response.EventDTO;
import com.fever.infrastructure.dto.response.EventResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    public static EventResponseDTO mapToEventResponseDTO(List<EventList> eventsBetweenDates) {
        List<EventDTO> eventDTOList = eventsBetweenDates.stream()
                .filter(ev -> ev.sellMode().contains("online"))
                .flatMap(eventList -> eventList.events().stream()
                        .filter(event -> event.error() == null)
                        .map(event -> mapToEventDTO(eventList, event)))
                .collect(Collectors.toList());

        List<Error> errorEvents = eventsBetweenDates.stream()
                .flatMap(eventList -> eventList.events().stream())
                .filter(event -> event.error() != null)
                .map(event -> new Error(
                        event.error().baseEventId(),
                        event.error().eventId(),
                        event.error().startDate(),
                        event.error().endDate()
                ))
                .collect(Collectors.toList());

        return new EventResponseDTO(new DataDTO(eventDTOList), errorEvents);
    }

    private static EventDTO mapToEventDTO(EventList eventList, Event event) {

        LocalDate startDate = event.eventStartDate() != null ? event.eventStartDate().toLocalDate() : null;
        LocalTime startTime = event.eventStartDate() != null ? event.eventStartDate().toLocalTime() : null;
        LocalDate endDate = event.eventEndDate() != null ? event.eventEndDate().toLocalDate() : null;
        LocalTime endTime = event.eventEndDate() != null ? event.eventEndDate().toLocalTime() : null;

        return new EventDTO(
                eventList.baseEventId(),
                eventList.title(),
                startDate,
                startTime,
                endDate,
                endTime,
                calculateMinPrice(event),
                calculateMaxPrice(event)

        );
    }

    private static BigDecimal calculateMaxPrice(Event event) {
        return event.zones() != null ? event.zones().stream()
                .map(Zone::price)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO) : BigDecimal.ZERO;

    }

    private static BigDecimal calculateMinPrice(Event event) {
        return event.zones() != null ? event.zones().stream()
                .map(Zone::price)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO) : BigDecimal.ZERO;
    }
}

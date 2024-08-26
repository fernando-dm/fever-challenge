package com.fever.application.usecases.eventMapper;

import com.fever.domain.model.Error;
import com.fever.domain.model.Event;
import com.fever.domain.model.EventList;
import com.fever.domain.model.Zone;
import com.fever.infrastructure.dto.response.EventResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventMapperTest {

    @Test
    public void mapToEventResponseDTO_withValidEvents_returns_correct_EventResponseDTO() {
        // given
        Zone zone1 = buildZone("1", 100, BigDecimal.valueOf(10.00), "Zone 1", true);
        Zone zone2 = buildZone("2", 50, BigDecimal.valueOf(20.00), "Zone 2", false);

        Event event1 = buildEvent(zone1, zone2, LocalDateTime.of(2021, 6, 30, 20, 0));
        Event event2 = buildEvent(zone1, zone2, LocalDateTime.of(2021, 7, 30, 20, 0));

        EventList eventList1 = new EventList("100", "online", "Evento 1", List.of(event1));
        EventList eventList2 = new EventList("200", "online", "Evento 2", List.of(event2));


        // When
        List<EventList> eventList = List.of(eventList1, eventList2);
        EventResponseDTO responseDTO = EventMapper.mapToEventResponseDTO(eventList);

        //Then
        assertEquals(2, responseDTO.dataDTO().events().size());
        assertTrue(responseDTO.dataDTO().events().stream().anyMatch(eventDTO -> eventDTO.id().equals("100")));
        assertTrue(responseDTO.dataDTO().events().stream().anyMatch(eventDTO -> eventDTO.title().equals("Evento 1")));
        assertEquals(BigDecimal.valueOf(10.00), responseDTO.dataDTO().events().get(0).min_price());
        assertEquals(BigDecimal.valueOf(20.00), responseDTO.dataDTO().events().get(0).max_price());
    }

    @Test
    public void mapToEventResponseDTO_withErrorEvents_returnsCorrectEventResponseDTO() {
        // given
        Error errorEvent1 = new Error("99999", "1", LocalDate.of(2021, 6, 30).toString(),
                LocalDate.of(2021, 6, 30).toString());
        Event event1 = new Event(
                "1",
                LocalDateTime.of(2021, 6, 30, 21, 0),
                LocalDateTime.of(2021, 6, 30, 22, 0),
                LocalDateTime.of(2021, 6, 30, 20, 0),
                LocalDateTime.of(2021, 6, 30, 22, 0),
                false,
                List.of(),
                errorEvent1
        );

        EventList eventList = new EventList("baseId", "online", "Test Event", List.of(event1));

        // When
        EventResponseDTO responseDTO = EventMapper.mapToEventResponseDTO(List.of(eventList));

        // Then
        assertEquals(0, responseDTO.dataDTO().events().size());
        assertEquals(1, responseDTO.error().size());
        assertEquals("1", responseDTO.error().get(0).eventId());
    }

    @Test
    public void mapToEventResponseDTO_with_two_events_and_one_error_returnsCorrectEventResponseDTO() {
        // Given
        Zone zone1 = buildZone("1", 100, BigDecimal.valueOf(10.00), "Zone 1", true);

        Error errorEvent1 = new Error("baseId", "1",
                LocalDate.of(2021, 6, 30).toString(),
                LocalDate.of(2021, 6, 30).toString());

        Event event1 = buildEvent(zone1, null, LocalDateTime.of(2021, 6, 30, 20, 0));

        Event event2 = new Event(
                "2",
                LocalDateTime.of(2021, 6, 30, 21, 0),
                LocalDateTime.of(2021, 6, 30, 22, 0),
                LocalDateTime.of(2021, 6, 30, 20, 0),
                LocalDateTime.of(2021, 6, 30, 22, 0),
                false,
                List.of(),
                errorEvent1
        );
        EventList eventList = new EventList("baseId", "online", "Test Event", List.of(event1, event2));

        // When
        EventResponseDTO responseDTO = EventMapper.mapToEventResponseDTO(List.of(eventList));

        // Then
        assertEquals(1, responseDTO.dataDTO().events().size());
        assertEquals(1, responseDTO.error().size());
        assertEquals("1", responseDTO.error().get(0).eventId());
    }

    private Zone buildZone(String zoneId, int capacity, BigDecimal price, String name, boolean numbered) {
        return new Zone(zoneId, capacity, price, name, numbered);
    }

    private static Event buildEvent(Zone zone1, Zone zone2, LocalDateTime eventStartDate) {
        ArrayList<Zone> zoneList = new ArrayList<>(List.of(zone1));
        if (zone2 != null)
            zoneList.add(zone2);

        return new Event(
                "1",
                eventStartDate,
                eventStartDate.plusHours(1),
                eventStartDate.plusHours(2),
                eventStartDate.plusHours(3),
                false,
                zoneList,
                null
        );
    }
}

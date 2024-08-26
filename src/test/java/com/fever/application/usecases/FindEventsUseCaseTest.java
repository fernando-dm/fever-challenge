package com.fever.application.usecases;

import com.fever.application.usecases.eventMapper.EventMapper;
import com.fever.domain.model.Event;
import com.fever.domain.model.EventList;
import com.fever.domain.model.Zone;
import com.fever.domain.repositories.FindEvents;
import com.fever.infrastructure.clients.MockClient;
import com.fever.infrastructure.dto.response.EventResponseDTO;
import com.fever.infrastructure.xmlhandler.XmlProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindEventsUseCaseTest {
    @Mock
    private XmlProcessor xmlProcessor;

    private MockClient mockClient;

    @InjectMocks
    private FindEventsUseCase findEventsUseCase;

    @Mock
    private FindEvents findEvents;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockClient = new MockClient(xmlProcessor);
        findEventsUseCase = new FindEventsUseCase(mockClient);
    }

    @Test
    public void getEventsBetween_validDates_returns_EventResponseDTO() {
        // Given
        LocalDate startDate = LocalDate.of(2021, 6, 30);
        LocalDate endDate = LocalDate.of(2021, 6, 30);
        List<EventList> eventLists = givenAValidEventList();

        when(xmlProcessor.extractEventList(any(), any(), any())).thenReturn(eventLists);

        // When
        EventResponseDTO responseDTO = findEventsUseCase.getEventsBetweenDates(startDate, endDate);

        // Then
        assertNotNull(responseDTO);
        assertEquals(EventMapper.mapToEventResponseDTO(eventLists), responseDTO);
    }

    @Test
    void nullStartDate_throwsRuntimeException() {
        // Given
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023, 8, 31);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                findEventsUseCase.getEventsBetweenDates(startDate, endDate)
        );

        assertEquals("java.lang.Exception: Invalid dates/time format", exception.getMessage());
        verify(findEvents, never()).getEventsBetweenDates(any(), any());
    }

    @Test
    void endDateBeforeStartDate_throwsRuntimeException() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 8, 31);
        LocalDate endDate = LocalDate.of(2023, 8, 1);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                findEventsUseCase.getEventsBetweenDates(startDate, endDate)
        );

        //Then
        assertEquals("java.lang.Exception: Invalid dates/time format", exception.getMessage());
        verify(findEvents, never()).getEventsBetweenDates(any(), any());
    }

    @Test
    void nullEndDate_throwsRuntimeException() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 8, 1);
        LocalDate endDate = null;

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                findEventsUseCase.getEventsBetweenDates(startDate, endDate)
        );

        //Then
        assertEquals("java.lang.Exception: Invalid dates/time format", exception.getMessage());
        verify(findEvents, never()).getEventsBetweenDates(any(), any());
    }

    private static List<EventList> givenAValidEventList() {
        Zone zone1 = new Zone("40", 240, BigDecimal.valueOf(10.00), "Platea", true);
        Zone zone2 = new Zone("38", 50, BigDecimal.valueOf(20.00), "Grada 2", false);
        Zone zone3 = new Zone("30", 90, BigDecimal.valueOf(30.00), "A28", true);

        Event event = new Event(
                "291",
                LocalDateTime.of(2021, 6, 30, 21, 0),
                LocalDateTime.of(2021, 6, 30, 22, 0),
                LocalDateTime.of(2020, 7, 1, 0, 0),
                LocalDateTime.of(2021, 6, 30, 20, 0),
                false,
                List.of(zone1, zone2, zone3),
                null
        );

        List<EventList> eventLists = List.of(
                new EventList("1", "online", "Megadeth", List.of(event)));
        return eventLists;
    }
}



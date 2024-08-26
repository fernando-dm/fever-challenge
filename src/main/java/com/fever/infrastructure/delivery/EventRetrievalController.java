package com.fever.infrastructure.delivery;

import com.fever.application.usecases.FindEventsUseCase;
import com.fever.infrastructure.dto.response.EventResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/1.0.0")
public class EventRetrievalController {

    private final FindEventsUseCase findEventsUseCase;

    public EventRetrievalController(FindEventsUseCase findEventsUseCase) {
        this.findEventsUseCase = findEventsUseCase;
    }

    @GetMapping("/search")
    public ResponseEntity<EventResponseDTO> listEvents(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        EventResponseDTO listEvents = findEventsUseCase.getEventsBetweenDates(startDate, endDate);
        return new ResponseEntity<>(listEvents, OK);
    }

}
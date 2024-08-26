package com.fever.infrastructure.xmlhandler;

import com.fever.domain.model.Error;
import com.fever.domain.model.Event;
import com.fever.domain.model.EventList;
import com.fever.domain.model.Zone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class XmlProcessor {

    public List<EventList> extractEventList(Document document, LocalDate startDate, LocalDate endDate) {
        List<EventList> eventLists = new ArrayList<>();
        NodeList baseEventNodes = document.getElementsByTagName("base_event");

        for (int i = 0; i < baseEventNodes.getLength(); i++) {
            Element baseEventElement = (Element) baseEventNodes.item(i);
            EventList eventList = createEventListDTO(baseEventElement, startDate, endDate);

            if (eventList != null) {
                eventLists.add(eventList);
            }
        }

        return eventLists;
    }

    private EventList createEventListDTO(Element baseEventElement, LocalDate startDate, LocalDate endDate) {
        String baseEventId = baseEventElement.getAttribute("base_event_id");
        String sellMode = baseEventElement.getAttribute("sell_mode");
        String title = baseEventElement.getAttribute("title");

        List<Event> events = extractEventDTOs(baseEventId, baseEventElement, startDate, endDate);

        return events.isEmpty() ? null : new EventList(baseEventId, sellMode, title, events);
    }

    private List<Event> extractEventDTOs(String baseEventId, Element baseEventElement, LocalDate startDate, LocalDate endDate) {
        List<Event> events = new ArrayList<>();
        NodeList eventNodes = baseEventElement.getElementsByTagName("event");

        for (int i = 0; i < eventNodes.getLength(); i++) {
            Element eventElement = (Element) eventNodes.item(i);
            Event event = createEventDTO(baseEventId, eventElement, startDate, endDate);
            events.add(event);
        }

        return events;
    }

    private Event createEventDTO(String baseEventId, Element eventElement, LocalDate startDate, LocalDate endDate) {
        LocalDateTime eventStartDate = parseXmlDateTime(eventElement.getAttribute("event_start_date"));
        LocalDateTime eventEndDate = parseXmlDateTime(eventElement.getAttribute("event_end_date"));

        if (isDateRangeValid(eventStartDate, eventEndDate, startDate, endDate)) {
            String eventId = eventElement.getAttribute("event_id");
            LocalDateTime sellFrom = parseXmlDateTime(eventElement.getAttribute("sell_from"));
            LocalDateTime sellTo = parseXmlDateTime(eventElement.getAttribute("sell_to"));
            boolean soldOut = Boolean.parseBoolean(eventElement.getAttribute("sold_out"));

            List<Zone> zones = extractZoneDTOs(eventElement);
            return new Event(eventId, eventStartDate, eventEndDate, sellFrom, sellTo, soldOut, zones, null);
        }

        log.error("There was a problem processing the base_event: {} event_id: {} with event_start_date: {} and event_end_date: {}",
                baseEventId,
                eventElement.getAttribute("event_id"),
                eventElement.getAttribute("event_start_date"),
                eventElement.getAttribute("event_end_date")
        );
        return new Event(null, null, null,
                null, null, false, null,
                new Error(
                        baseEventId,
                        eventElement.getAttribute("event_id"),
                        eventElement.getAttribute("event_start_date"),
                        eventElement.getAttribute("event_end_date")));
    }

    private List<Zone> extractZoneDTOs(Element eventElement) {
        List<Zone> zones = new ArrayList<>();
        NodeList zoneNodes = eventElement.getElementsByTagName("zone");

        for (int i = 0; i < zoneNodes.getLength(); i++) {
            Element zoneElement = (Element) zoneNodes.item(i);
            Zone zone = createZoneDTO(zoneElement);
            zones.add(zone);
        }

        return zones;
    }

    private Zone createZoneDTO(Element zoneElement) {
        String zoneId = zoneElement.getAttribute("zone_id");
        int capacity = Integer.parseInt(zoneElement.getAttribute("capacity"));
        BigDecimal price = new BigDecimal(zoneElement.getAttribute("price"));
        String name = zoneElement.getAttribute("name");
        boolean numbered = Boolean.parseBoolean(zoneElement.getAttribute("numbered"));

        return new Zone(zoneId, capacity, price, name, numbered);
    }

    private boolean isDateRangeValid(LocalDateTime eventStartDate, LocalDateTime eventEndDate, LocalDate startDate, LocalDate endDate) {
        if (eventStartDate == null || eventEndDate == null) {
            return false;
        }
        return (!eventStartDate.toLocalDate().isBefore(startDate)) &&
                (!eventEndDate.toLocalDate().isAfter(endDate));
    }


    private LocalDateTime parseXmlDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("Invalid date/time format for: {} error : {} ", dateTimeStr, e.getMessage());
            return null;
        }
    }
}

package com.fever.domain.repositories;

import com.fever.domain.model.EventList;

import java.time.LocalDate;
import java.util.List;

public interface FindEvents {
    List<EventList> getEventsBetweenDates(LocalDate startDate, LocalDate endDate) ;
}

package pro.tsep.service;

import pro.tsep.entity.Event;

import java.util.List;

public interface EventService {

    void createEvent(Event event);

    List<Event> getAllEvents();

    Event updateEvent(Long id, Event event);
}

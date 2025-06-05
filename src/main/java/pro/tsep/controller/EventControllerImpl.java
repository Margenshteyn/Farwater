package pro.tsep.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pro.tsep.entity.Event;
import pro.tsep.service.EventService;

import java.util.List;

@RestController
@Slf4j
public class EventControllerImpl implements EventController {

    private final EventService eventService;

    public EventControllerImpl(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public ResponseEntity<Void> createEvent(Event event) {
        log.info("Поставлен POST-запрос /api/events с данными: {}", event);
        eventService.createEvent(event);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Event>> getAllEvents() {
        log.info("Получен GET-запрос /api/events");
        List<Event> events = eventService.getAllEvents();
        if (events != null) {
            return ResponseEntity.ok(events);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Event> updateEvent(Long id, Event event) {
        log.info("Поставлен PUT-запрос /api/events/{} с данными: {}", id, event);
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }
}

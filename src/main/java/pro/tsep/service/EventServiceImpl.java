package pro.tsep.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.tsep.entity.Event;
import pro.tsep.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    public EventServiceImpl(EventRepository eventRepository, NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void createEvent(Event event) {
        log.info("Creating event: {}", event.getMessage());
        eventRepository.save(event);
        // Запуск логики оповещения
        notificationService.handleEvent(event);
    }

    @Override
    public List<Event> getAllEvents() {
        log.info("Getting all events");
        return eventRepository.findAll();
    }

    @Override
    public Event updateEvent(Long id, Event event) {
        log.info("Updating event with ID: {}", id);
        if (eventRepository.findById(id).isPresent()) {
            return eventRepository.save(event);
        } else throw new RuntimeException("Event not found");
    }
}

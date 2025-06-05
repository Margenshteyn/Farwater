package pro.tsep.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.tsep.entity.Event;

import java.util.List;

@RequestMapping("/api/events")
public interface EventController {
    @PostMapping
    ResponseEntity<Void> createEvent(@RequestBody Event event);

    @GetMapping
    ResponseEntity<List<Event>> getAllEvents();

    @PutMapping("/{id}")
    ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event);
}

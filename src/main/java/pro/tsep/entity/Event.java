package pro.tsep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private LocalDateTime eventTime;

    // Явный конструктор, т.к. @AllArgsConstructor почему-то не сработал
    public Event(String message, LocalDateTime eventTime) {
        this.message = message;
        this.eventTime = eventTime;
    }
}

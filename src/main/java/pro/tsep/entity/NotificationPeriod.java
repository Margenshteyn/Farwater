package pro.tsep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // День недели (например, MONDAY)
    private DayOfWeek dayOfWeek;

    // Время начала периода
    private LocalTime startTime;

    // Время окончания периода
    private LocalTime endTime;

    // Время для отправки уведомления в этот день (если событие вне периода)
    private LocalTime preferredTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

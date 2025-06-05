package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pro.tsep.entity.Event;
import pro.tsep.entity.NotificationPeriod;
import pro.tsep.entity.User;
import pro.tsep.repository.UserRepository;
import pro.tsep.service.NotificationServiceImpl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private Logger logger;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotificationImmediatelyWhenInNotificationPeriod() {
        // Подготовим данные
        User user = prepareUser(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
        Event event = new Event("Утечка масла", LocalDateTime.of(2023, 7, 11, 10, 30)); // вторник, 10:30

        // Имитация получения пользователя из репозитория
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Выполняем отправку уведомления
        notificationService.sendNotificationToUser(user, event);

        // Проверяем, что уведомление отправилось немедленно
        verify(logger).info("{} Пользователю {} отправлено оповещение с текстом: {}",
                event.getEventTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                user.getFullName(), event.getMessage());
        verify(messagingTemplate).convertAndSend(eq("/topic/notifications"), any(Event.class));
    }

    @Test
    void testScheduleNotificationWhenOutsideNotificationPeriod() {
        // Подготовим данные пользователя с периодом, который НЕ совпадает с событием
        User user = prepareUser(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
        Event event = new Event("Конец света", LocalDateTime.of(2023, 7, 11, 14, 0)); // Вторник, 14:00

        // Имитация пользователя из репозитория
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Выполняем отправку уведомления
        notificationService.sendNotificationToUser(user, event);

        // Проверяем, что уведомление запланировано на другой день
        verify(logger).info("{} Пользователь получит уведомление в {}. Причина: событие произошло вне времени информирования.",
                event.getEventTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "09:00"); // Предполагаемое оптимальное время (предпочтительное время периода)
    }

    @Test
    void testNoSuitableNotificationPeriod() {
        // Подготовим данные пользователя с ограниченным периодом (без возможности повторения)
        User user = prepareUser(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
        Event event = new Event("Ошибка сервера", LocalDateTime.of(2023, 7, 11, 14, 0)); // Вторник, 14:00

        // Имитация пользователя из репозитория
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Выполняем отправку уведомления
        notificationService.sendNotificationToUser(user, event);

        // Проверяем отсутствие допустимого периода для уведомления
        verify(logger).warn("Нет доступных интервалов для уведомления пользователя: {}", user.getFullName());
    }

    private User prepareUser(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        NotificationPeriod period = NotificationPeriod.builder()
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .preferredTime(LocalTime.of(9, 0))
                .build();

        return User.builder()
                .fullName("Иван Петров")
                .notificationPeriods(List.of(period))
                .build();
    }
}

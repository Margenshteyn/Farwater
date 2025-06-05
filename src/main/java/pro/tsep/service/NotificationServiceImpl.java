package pro.tsep.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pro.tsep.entity.Event;
import pro.tsep.entity.NotificationPeriod;
import pro.tsep.entity.User;
import pro.tsep.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Обработка события: отправка уведомлений всем пользователям.
     */
    @Override
    public void handleEvent(Event event) {
        log.info("Событие произошло в {}", event.getEventTime());

        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getNotificationPeriods().isEmpty()) {
                sendNotificationToUser(user, event);
            }
        }
    }

    /**
     * Определяет, отправлять ли уведомление пользователю немедленно или ждать подходящего времени.
     */
    @Override
    public void sendNotificationToUser(User user, Event event) {
        // Проверяем, попадает ли событие в временной диапазон уведомления пользователя
        boolean withinNotificationPeriod = isWithinNotificationPeriod(user, event);

        if (withinNotificationPeriod) {
            // Отправляем уведомление сразу, поскольку событие пришло вовремя
            sendImmediateNotification(user, event);
        } else {
            // Если событие вне указанного периода, ищем ближайшую возможную точку отправки уведомления
            LocalTime nextAvailableTime = findNextAvailableTimeForUser(user, event);
            if (nextAvailableTime != null) {
                log.info("{} Пользователь получит уведомление в {}. Причина: событие произошло вне времени информирования.",
                        event.getEventTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), nextAvailableTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                log.warn("Нет доступных интервалов для уведомления пользователя: {}", user.getFullName());
            }
        }
    }

    /**
     * Непосредственно отправляет уведомление пользователю через WebSocket.
     */
    private void sendImmediateNotification(User user, Event event) {
        String formattedDate = event.getEventTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("{} Пользователю {} отправлено оповещение с текстом: {}",
                formattedDate, user.getFullName(), event.getMessage());
        messagingTemplate.convertAndSend("/topic/notifications", event);
    }

    /**
     * Ищем ближайшую доступную точку времени для отправки уведомления пользователю.
     */
    private LocalTime findNextAvailableTimeForUser(User user, Event event) {
        LocalDate currentDate = LocalDate.now();

        while (true) {
            for (NotificationPeriod period : user.getNotificationPeriods()) {
                if (period.getDayOfWeek() == currentDate.getDayOfWeek()) {
                    // Проверяем наличие совпадения дня недели
                    if (isWithinTimeRange(currentDate.atTime(period.getStartTime()),
                            currentDate.atTime(period.getEndTime()))) {
                        return period.getPreferredTime(); // Возврат предпочитаемого времени
                    }
                }
            }
            currentDate = currentDate.plusDays(1); // Переход к следующей дате
        }
    }

    /**
     * Проверяет, попадают ли указанные часы в установленный промежуток времени.
     */
    private boolean isWithinTimeRange(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        return !(now.isBefore(start) || now.isAfter(end));
    }

    /**
     * Проверяет, входит ли событие в временной интервал одного из периодов уведомления пользователя.
     */
    private boolean isWithinNotificationPeriod(User user, Event event) {
        LocalDateTime eventTime = event.getEventTime();
        for (NotificationPeriod period : user.getNotificationPeriods()) {
            if (period.getDayOfWeek() == eventTime.getDayOfWeek()
                    && isWithinTimeRange(eventTime.withSecond(0).withNano(0),
                    period.getStartTime(),
                    period.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Вспомогательная функция для определения попадания времени в диапазон.
     */
    private boolean isWithinTimeRange(LocalDateTime eventTime, LocalTime startTime, LocalTime endTime) {
        LocalDateTime start = eventTime.toLocalDate().atTime(startTime);
        LocalDateTime end = eventTime.toLocalDate().atTime(endTime);
        return !eventTime.isBefore(start) && !eventTime.isAfter(end);
    }

}

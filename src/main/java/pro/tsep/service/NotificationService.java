package pro.tsep.service;

import pro.tsep.entity.Event;
import pro.tsep.entity.User;

public interface NotificationService {

    void handleEvent(Event event);
    void sendNotificationToUser(User user, Event event);
}

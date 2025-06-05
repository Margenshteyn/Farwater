package pro.tsep.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pro.tsep.entity.User;
import pro.tsep.service.UserService;

import java.util.List;

@RestController
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Получен GET-запрос /api/users");
        List<User> users = userService.getAllUsers();
        if (users != null) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<User> createUser(User user) {
        log.info("Поставлен POST-запрос /api/users с данными: {}", user);
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Override
    public ResponseEntity<User> updateUser(Long id, User user) {
        log.info("Поставлен PUT-запрос /api/users/{} с данными: {}", id, user);
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        log.info("Поставлен DELETE-запрос /api/users/{} для удаления", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

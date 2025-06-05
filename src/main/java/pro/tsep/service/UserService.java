package pro.tsep.service;

import pro.tsep.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}

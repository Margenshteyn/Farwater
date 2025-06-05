package pro.tsep.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.tsep.entity.User;
import pro.tsep.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        log.info("Creating user: {}", user.getFullName());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("Updating user with ID: {}", id);
        if (userRepository.findById(id).isPresent()) {
            return userRepository.save(user);
        } else throw new RuntimeException("User not found");
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }

}

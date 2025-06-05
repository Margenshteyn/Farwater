package pro.tsep.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.tsep.entity.User;

import java.util.List;

@RequestMapping("/api/users")
public interface UserController {
    @GetMapping
    ResponseEntity<List<User>> getAllUsers();

    @PostMapping
    ResponseEntity<User> createUser(@RequestBody User user);

    @PutMapping("/{id}")
    ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);

}

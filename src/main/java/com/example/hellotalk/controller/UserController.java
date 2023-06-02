package com.example.hellotalk.controller;

import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/ht/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> getUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Object> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId, @RequestBody User user) {
        return new ResponseEntity<>(userService.updateUser(userId, user), HttpStatus.OK);
    }

    @DeleteMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> deleteUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping({"{userId}/like/{momentId}", "{userId}/like/{momentId}/"}) // todo: remove user id and move this to like moment class. 02/06/2023
    public ResponseEntity<Object> likeMoment(@PathVariable UUID userId, @PathVariable UUID momentId) {
        return new ResponseEntity<>(userService.likeMoment(userId, momentId), HttpStatus.CREATED);
    }
}

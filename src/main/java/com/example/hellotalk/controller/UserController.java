package com.example.hellotalk.controller;

import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ht/user")
public class UserController {

    @Autowired UserService userService;

    @GetMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> getUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId, @RequestBody User user) {
        return new ResponseEntity<>(userService.updateUser(userId, user), HttpStatus.OK);
    }

}

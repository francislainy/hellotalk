package com.example.hellotalk.controller;

import com.example.hellotalk.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ht/user")
public class UserController {

    @Autowired UserService userService;

    @GetMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> getUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

}

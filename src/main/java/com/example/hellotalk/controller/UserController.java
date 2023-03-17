package com.example.hellotalk.controller;

import com.example.hellotalk.entity.user.LikeEntity;
import com.example.hellotalk.entity.user.ResultInfo;
import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/ht/users")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    @PostMapping({"{userId}/like/{momentId}", "{userId}/like/{momentId}/"})
    public ResponseEntity<Object> likeMoment(@PathVariable UUID userId, @PathVariable UUID momentId) {

        LikeEntity likeEntity = userService.likeMoment(userId, momentId);
        ResultInfo resultInfo = ResultInfo.builder()
                .id(likeEntity.getId())
                .userId(likeEntity.getUserEntity().getId())
                .momentId(likeEntity.getMomentEntity().getId())
                .build();
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "Moment liked successfully");
        map.put("data", resultInfo);

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
}

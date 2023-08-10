package com.example.hellotalk.controller.followship;

import com.example.hellotalk.model.followship.Followship;
import com.example.hellotalk.service.FollowshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/ht/followship")
@RequiredArgsConstructor
public class FollowshipController {

    private final FollowshipService followshipService;

    @GetMapping({"/{followshipId}", "/{followshipId}/"})
    public ResponseEntity<Object> getFollowship(@PathVariable UUID followshipId) {
        return new ResponseEntity<>(followshipService.getFollowship(followshipId), HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Object> getAllFollowships() {
        return new ResponseEntity<>(followshipService.getAllFollowships(), HttpStatus.OK);
    }

    @GetMapping({"/from/user/{userId}", "/from/user/{userId}/"})
    public ResponseEntity<Object> getAllFollowshipsFromUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(followshipService.getAllFollowshipsFromUser(userId), HttpStatus.OK);
    }

    @GetMapping({"/to/user/{userId}", "/to/user/{userId}/"})
    public ResponseEntity<Object> getAllFollowshipsToUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(followshipService.getAllFollowshipsToUser(userId), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createFollowship(@RequestBody Followship followship) {
        return new ResponseEntity<>(followshipService.createFollowship(followship), HttpStatus.CREATED);
    }
}

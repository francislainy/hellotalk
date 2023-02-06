package com.example.hellotalk.controller;

import com.example.hellotalk.model.FollowingRequest;
import com.example.hellotalk.service.FollowingRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ht/follow")
public class FollowingRequestController {

    final FollowingRequestService followingRequestService;

    public FollowingRequestController(FollowingRequestService followingRequestService) {
        this.followingRequestService = followingRequestService;
    }

    @GetMapping({"/{followingRequestId}", "/{followingRequestId}/"})
    public ResponseEntity<Object> getFollowingRequest(@PathVariable UUID followingRequestId) {
        return new ResponseEntity<>(followingRequestService.getFollowingRequest(followingRequestId), HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Object> getAllFollowingRequests() {
        return new ResponseEntity<>(followingRequestService.getAllFollowingRequests(), HttpStatus.OK);
    }

    @GetMapping({"/from/user/{userId}", "/from/user/{userId}/"})
    public ResponseEntity<Object> getAllFollowingRequestsFromUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(followingRequestService.getAllFollowingRequestsFromUser(userId), HttpStatus.OK);
    }

    @GetMapping({"/to/user/{userId}", "/to/user/{userId}/"})
    public ResponseEntity<Object> getAllFollowingRequestsToUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(followingRequestService.getAllFollowingRequestsToUser(userId), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createFollowingRequest(@RequestBody FollowingRequest followingRequest) {

        return new ResponseEntity<>(followingRequestService.createFollowingRequest(followingRequest), HttpStatus.CREATED);
    }
}

package com.example.hellotalk.controller;

import com.example.hellotalk.model.user.FollowingRequest;
import com.example.hellotalk.service.user.FollowingRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ht/follow")
public class FollowingRequestController {

    final FollowingRequestService followingRequestService;

    public FollowingRequestController(FollowingRequestService followingRequestService) {
        this.followingRequestService = followingRequestService;
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createFollowingRequest(@RequestBody FollowingRequest followingRequest) {

        return new ResponseEntity<>(followingRequestService.createFollowingRequest(followingRequest), HttpStatus.CREATED);
    }
}

package com.example.hellotalk.controller;

import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.service.moment.MomentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ht/moments")
public class MomentController {

    final MomentService momentService;

    public MomentController(MomentService momentService) {
        this.momentService = momentService;
    }

    @GetMapping({"/{momentId}", "/{momentId}/"})
    public ResponseEntity<Object> getMoment(@PathVariable UUID momentId) {
        return new ResponseEntity<>(momentService.getMoment(momentId), HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Object> getAllMoments() {
        return new ResponseEntity<>(momentService.getAllMoments(), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createMoment(@RequestBody Moment moment) {
        return new ResponseEntity<>(momentService.createMoment(moment), HttpStatus.CREATED);
    }

    @PutMapping({"/{momentId}", "/{momentId}/"})
    public ResponseEntity<Object> updateMoment(@PathVariable UUID momentId, @RequestBody Moment moment) {
        return new ResponseEntity<>(momentService.updateMoment(momentId, moment), HttpStatus.OK);
    }

    @DeleteMapping({"/{momentId}", "/{momentId}/"})
    public ResponseEntity<Object> deleteMoment(@PathVariable UUID momentId) {
        return new ResponseEntity<>(momentService.deleteMoment(momentId), HttpStatus.PARTIAL_CONTENT);
    }
}

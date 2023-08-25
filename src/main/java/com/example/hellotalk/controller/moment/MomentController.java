package com.example.hellotalk.controller.moment;

import com.example.hellotalk.model.moment.Moment;
import com.example.hellotalk.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/ht/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @GetMapping({"/{momentId}", "/{momentId}/"})
    public ResponseEntity<Object> getMoment(@PathVariable UUID momentId) {
        return new ResponseEntity<>(momentService.getMoment(momentId), HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Object> getAllMoments() {
        return new ResponseEntity<>(momentService.getAllMoments(), HttpStatus.OK);
    }

    @GetMapping({"/user", "/user/"})
    public ResponseEntity<Object> getAllMomentsForUser(@RequestParam("userId") UUID userId) {
        return new ResponseEntity<>(momentService.getAllMomentsForUser(userId), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createMoment(@RequestBody Moment moment) {
        return new ResponseEntity<>(momentService.createMoment(moment), HttpStatus.CREATED);
    }

    @PutMapping({"/{momentId}", "/{momentId}/"})
    public ResponseEntity<Object> updateMoment(@PathVariable UUID momentId, @RequestBody Moment moment) {
        return new ResponseEntity<>(momentService.updateMoment(momentId, moment), HttpStatus.OK);
    }

    @PostMapping({"/{momentId}/like", "/{momentId}/like/"})
    public ResponseEntity<Object> likeMoment(@PathVariable UUID momentId) {
        return new ResponseEntity<>(momentService.likeMoment(momentId), HttpStatus.CREATED);
    }

    @DeleteMapping({"/{momentId}", "/{momentId}/"})
    public ResponseEntity<Object> deleteMoment(@PathVariable UUID momentId) {
        momentService.deleteMoment(momentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

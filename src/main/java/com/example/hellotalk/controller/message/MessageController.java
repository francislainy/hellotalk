package com.example.hellotalk.controller.message;

import com.example.hellotalk.model.message.Message;
import com.example.hellotalk.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/ht/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping({"/{messageId}", "/{messageId}/"})
    public ResponseEntity<Object> getMessage(@PathVariable UUID messageId) {
        return new ResponseEntity<>(messageService.getMessage(messageId), HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Object> getAllMessages() {
        return new ResponseEntity<>(messageService.getAllMessages(), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createMessage(@RequestBody Message message) {
        return new ResponseEntity<>(messageService.createMessage(message), HttpStatus.CREATED);
    }

    @PutMapping({"/{messageId}", "/{messageId}/"})
    public ResponseEntity<Object> updateMessage(@PathVariable UUID messageId, @RequestBody Message message) {
        return new ResponseEntity<>(messageService.updateMessage(messageId, message), HttpStatus.OK);
    }

    @DeleteMapping({"/{messageId}", "/{messageId}/"})
    public ResponseEntity<Object> deleteMessage(@PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping({"/chats/{chatId}", "/chats/{chatId}/"})
    public ResponseEntity<Object> getChat(@PathVariable UUID chatId) {
        return new ResponseEntity<>(messageService.getChat(chatId), HttpStatus.OK);
    }

    @GetMapping({"/chats", "/chats/"})
    public ResponseEntity<Object> getChats() {
        return new ResponseEntity<>(messageService.getChats(), HttpStatus.OK);
    }
}

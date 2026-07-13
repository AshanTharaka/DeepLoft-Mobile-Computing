package com.deeploft.backend.controller;

import com.deeploft.backend.model.Message;
import com.deeploft.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/{user1}/{user2}")
    public List<Message> getChat(@PathVariable String user1, @PathVariable String user2) {
        return messageService.getChat(user1, user2);
    }

    @GetMapping("/conversations/{email}")
    public List<Message> getConversations(@PathVariable String email) {
        return messageService.getConversations(email);
    }

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }
}
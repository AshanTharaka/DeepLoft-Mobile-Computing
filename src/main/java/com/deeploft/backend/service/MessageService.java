package com.deeploft.backend.service;

import com.deeploft.backend.model.Message;
import com.deeploft.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getChat(String user1, String user2) {
        return messageRepository.findBySenderEmailAndReceiverEmailOrSenderEmailAndReceiverEmailOrderByTimestampAsc(
                user1, user2, user2, user1);
    }

    public Message sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        return messageRepository.save(message);
    }
}
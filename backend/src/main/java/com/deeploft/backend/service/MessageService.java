package com.deeploft.backend.service;

import com.deeploft.backend.model.Message;
import com.deeploft.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getChat(String user1, String user2) {
        return messageRepository.findBySenderEmailAndReceiverEmailOrSenderEmailAndReceiverEmailOrderByTimestampAsc(
                user1, user2, user2, user1);
    }

    public List<Message> getConversations(String email) {
        List<Message> allMessages = messageRepository.findAll();
        
        // Group by conversation partner and take the latest message
        Map<String, Message> latestMessages = new HashMap<>();
        
        for (Message m : allMessages) {
            if (m.getSenderEmail().equalsIgnoreCase(email) || m.getReceiverEmail().equalsIgnoreCase(email)) {
                String otherUser = m.getSenderEmail().equalsIgnoreCase(email) ? m.getReceiverEmail() : m.getSenderEmail();
                Message existing = latestMessages.get(otherUser);
                if (existing == null || m.getTimestamp().isAfter(existing.getTimestamp())) {
                    latestMessages.put(otherUser, m);
                }
            }
        }
        
        return latestMessages.values().stream()
                .sorted(Comparator.comparing(Message::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public Message sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        return messageRepository.save(message);
    }
}
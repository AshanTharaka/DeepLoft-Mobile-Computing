package com.deeploft.backend.repository;

import com.deeploft.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderEmailAndReceiverEmailOrSenderEmailAndReceiverEmailOrderByTimestampAsc(
            String s1, String r1, String s2, String r2);
    
    // To list unique conversations for a user
    List<Message> findBySenderEmailOrReceiverEmailOrderByTimestampDesc(String senderEmail, String receiverEmail);
}
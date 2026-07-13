package com.deeploft.backend.repository;

import com.deeploft.backend.model.PayoutTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayoutTransactionRepository extends JpaRepository<PayoutTransaction, Long> {
    List<PayoutTransaction> findByRecipientName(String recipientName);
}
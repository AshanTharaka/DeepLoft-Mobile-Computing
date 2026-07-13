package com.deeploft.backend.repository;

import com.deeploft.backend.model.SavedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavedCardRepository extends JpaRepository<SavedCard, Long> {
    List<SavedCard> findByUserEmail(String email);
}
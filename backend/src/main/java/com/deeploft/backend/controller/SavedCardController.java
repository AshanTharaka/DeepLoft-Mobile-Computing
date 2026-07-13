package com.deeploft.backend.controller;

import com.deeploft.backend.model.SavedCard;
import com.deeploft.backend.repository.SavedCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-cards")
public class SavedCardController {

    @Autowired
    private SavedCardRepository savedCardRepository;

    @GetMapping("/{email}")
    public List<SavedCard> getSavedCards(@PathVariable String email) {
        return savedCardRepository.findByUserEmail(email);
    }

    @PostMapping
    public SavedCard saveCard(@RequestBody SavedCard card) {
        return savedCardRepository.save(card);
    }
}
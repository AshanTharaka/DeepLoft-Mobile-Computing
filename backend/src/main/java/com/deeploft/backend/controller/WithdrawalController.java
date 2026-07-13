package com.deeploft.backend.controller;

import com.deeploft.backend.model.WithdrawalRequest;
import com.deeploft.backend.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @GetMapping("/balance/{name}")
    public Map<String, Double> getBalance(@PathVariable String name) {
        return Map.of("balance", withdrawalService.getInstructorBalance(name));
    }

    @PostMapping
    public WithdrawalRequest create(@RequestBody WithdrawalRequest request) {
        return withdrawalService.createRequest(request);
    }

    @GetMapping("/pending")
    public List<WithdrawalRequest> getPending() {
        return withdrawalService.getAllPending();
    }

    @GetMapping("/instructor/{email}")
    public List<WithdrawalRequest> getByInstructor(@PathVariable String email) {
        return withdrawalService.getByInstructor(email);
    }

    @PutMapping("/{id}")
    public WithdrawalRequest updateStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String comment) {
        return withdrawalService.updateStatus(id, status, comment);
    }
}
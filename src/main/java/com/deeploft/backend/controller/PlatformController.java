package com.deeploft.backend.controller;

import com.deeploft.backend.model.PlatformSettings;
import com.deeploft.backend.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform")
public class PlatformController {

    @Autowired
    private PlatformService platformService;

    @GetMapping("/settings")
    public PlatformSettings getSettings() {
        return platformService.getSettings();
    }

    @PostMapping("/settings")
    public PlatformSettings updateSettings(@RequestBody PlatformSettings settings) {
        return platformService.updateSettings(settings);
    }

    @GetMapping("/total-commission")
    public double getTotalCommission() {
        return platformService.getTotalCommission();
    }
}
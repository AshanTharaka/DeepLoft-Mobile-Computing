package com.deeploft.backend.service;

import com.deeploft.backend.model.Enrollment;
import com.deeploft.backend.model.PlatformSettings;
import com.deeploft.backend.repository.EnrollmentRepository;
import com.deeploft.backend.repository.PlatformSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatformService {

    @Autowired
    private PlatformSettingsRepository settingsRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public PlatformSettings getSettings() {
        return settingsRepository.findById(1L).orElse(new PlatformSettings());
    }

    public PlatformSettings updateSettings(PlatformSettings settings) {
        settings.setId(1L);
        return settingsRepository.save(settings);
    }

    public double getTotalCommission() {
        return enrollmentRepository.findAll().stream()
                .mapToDouble(Enrollment::getPlatformCommission)
                .sum();
    }
}
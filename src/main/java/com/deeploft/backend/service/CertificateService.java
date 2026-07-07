package com.deeploft.backend.service;

import com.deeploft.backend.model.Certificate;
import com.deeploft.backend.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public List<Certificate> getCertificatesByUser(String email) {
        return certificateRepository.findByUserEmail(email);
    }

    public Certificate issueCertificate(Certificate certificate) {
        certificate.setIssuedDate(LocalDateTime.now());
        // Check if already exists to avoid duplicates
        List<Certificate> existing = certificateRepository.findByUserEmail(certificate.getUserEmail());
        boolean alreadyIssued = existing.stream()
                .anyMatch(c -> c.getCourseTitle().equals(certificate.getCourseTitle()));
        
        if (alreadyIssued) return existing.stream()
                .filter(c -> c.getCourseTitle().equals(certificate.getCourseTitle()))
                .findFirst().get();
                
        return certificateRepository.save(certificate);
    }
}
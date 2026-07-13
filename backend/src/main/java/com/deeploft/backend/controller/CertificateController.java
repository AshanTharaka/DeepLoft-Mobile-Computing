package com.deeploft.backend.controller;

import com.deeploft.backend.model.Certificate;
import com.deeploft.backend.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/user/{email}")
    public List<Certificate> getCertificates(@PathVariable String email) {
        return certificateService.getCertificatesByUser(email);
    }

    @PostMapping
    public Certificate issueCertificate(@RequestBody Certificate certificate) {
        return certificateService.issueCertificate(certificate);
    }
}
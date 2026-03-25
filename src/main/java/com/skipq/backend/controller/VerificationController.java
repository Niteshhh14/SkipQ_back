package com.skipq.backend.controller;

import com.skipq.backend.dto.VerifyResponse;
import com.skipq.backend.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/{orderId}")
    public ResponseEntity<VerifyResponse> verify(@PathVariable Long orderId) {
        return ResponseEntity.ok(verificationService.verify(orderId));
    }
}

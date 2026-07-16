package com.pedrogio.wedding.payment;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/api/payments/create")
    public ResponseEntity<PaymentCreateResponse> create(@Valid @RequestBody PaymentCreateRequest request) {
        return ResponseEntity.ok(service.create(request));
    }
}

package com.kartingrm.controller;

import com.kartingrm.dto.PaymentRequestDTO;
import com.kartingrm.entity.Payment;
import com.kartingrm.repository.PaymentRepository;
import com.kartingrm.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService svc;

    @PostMapping
    public ResponseEntity<Payment> pay(@Valid @RequestBody PaymentRequestDTO dto){
        return ResponseEntity.ok(svc.pay(dto));
    }
}


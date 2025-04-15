package com.kartingrm.controller;

import com.kartingrm.entity.Payment;
import com.kartingrm.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentRepository.save(payment);
    }

    @GetMapping
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }
}

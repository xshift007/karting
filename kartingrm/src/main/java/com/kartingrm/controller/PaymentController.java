package com.kartingrm.controller;

import com.kartingrm.dto.PaymentRequestDTO;
import com.kartingrm.entity.Payment;
import com.kartingrm.repository.PaymentRepository;
import com.kartingrm.service.PaymentService;
import com.kartingrm.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_PDF;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService svc;
    private final PaymentRepository paymentRepository;
    private final PdfService pdfService;

    @PostMapping
    public ResponseEntity<Payment> pay(@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(svc.pay(dto));
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no existe"));
        byte[] pdf = pdfService.buildReceipt(p.getReservation(), p);
        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=\"comprobante.pdf\"")
                .contentType(APPLICATION_PDF)
                .body(pdf);
    }
}

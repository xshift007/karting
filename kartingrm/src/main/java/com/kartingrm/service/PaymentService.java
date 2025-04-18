package com.kartingrm.service;

import com.kartingrm.dto.PaymentRequestDTO;
import com.kartingrm.entity.Payment;
import com.kartingrm.entity.Reservation;
import com.kartingrm.repository.PaymentRepository;
import com.kartingrm.repository.ReservationRepository;
import com.kartingrm.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationRepository reservations;
    private final PaymentRepository payments;
    private final PdfService pdf;
    private final MailService mail;

    @Transactional
    public Payment pay(PaymentRequestDTO dto) {
        Reservation r = reservations.findById(dto.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reserva no existe"));

        Payment p = new Payment();
        p.setReservation(r);
        p.setPaymentMethod(dto.method());
        p.setVatAmount(r.getFinalPrice() * 0.19);
        p.setFinalAmountInclVat(r.getFinalPrice() + p.getVatAmount());

        payments.save(p);

        // PDF + mail
        byte[] pdfBytes = pdf.buildReceipt(r, p);
        mail.sendReceipt(r.getClient().getEmail(), pdfBytes);
        return p;
    }
}

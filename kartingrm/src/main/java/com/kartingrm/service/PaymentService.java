package com.kartingrm.service;

import com.kartingrm.dto.PaymentRequestDTO;
import com.kartingrm.entity.*;
import com.kartingrm.repository.PaymentRepository;
import com.kartingrm.repository.ReservationRepository;
import com.kartingrm.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationRepository reservations;
    private final PaymentRepository     payments;
    private final PdfService            pdf;
    private final MailService           mail;
    private final ClientService         clients;

    /** Registra el pago y, al confirmar la transacción, envía el comprobante. */
    @Transactional
    public Payment pay(PaymentRequestDTO dto){

        Reservation r = reservations.findById(dto.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reserva no existe"));

        if (r.getStatus() != ReservationStatus.PENDING)
            throw new IllegalStateException("La reserva ya fue pagada o cancelada");

        Payment p = new Payment();
        p.setReservation(r);
        p.setPaymentMethod(dto.method());
        p.setVatAmount(r.getFinalPrice() * 0.19);
        p.setFinalAmountInclVat(r.getFinalPrice() + p.getVatAmount());

        payments.save(p);

        // confirma la reserva y aumenta visitas del cliente
        r.setStatus(ReservationStatus.CONFIRMED);
        reservations.save(r);
        clients.incrementVisits(r.getClient());

        /* envío de correo fuera de la transacción ------------------------- */
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override public void afterCommit() {
                        byte[] pdfBytes = pdf.buildReceipt(r, p);
                        mail.sendReceipt(r, pdfBytes);
                    }
                });

        return p;
    }

    @Transactional(readOnly = true)
    public byte[] generateReceipt(Long paymentId) {

        Payment p = payments.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no existe"));

        p.getReservation().getParticipantsList().size(); // inicializa colección
        return pdf.buildReceipt(p.getReservation(), p);
    }
}

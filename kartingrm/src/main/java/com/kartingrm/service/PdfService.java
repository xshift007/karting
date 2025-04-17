package com.kartingrm.service;

import com.kartingrm.entity.Payment;
import com.kartingrm.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class PdfService {
    public byte[] buildReceipt(Reservation r, Payment p) {
        // usar OpenPDF o iText 2.1.7
        return new byte[0];
    }
}

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender sender;

    public void sendReceipt(String to, byte[] pdf) {
        MimeMessage mime = sender.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(mime, true);
        h.setTo(to); h.setSubject("Comprobante de Reserva KartingRM");
        h.setText("Adjunto encontrará su comprobante.");
        h.addAttachment("comprobante.pdf", new ByteArrayResource(pdf));
        sender.send(mime);
    }
}

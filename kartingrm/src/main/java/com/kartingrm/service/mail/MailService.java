package com.kartingrm.service.mail;

import com.kartingrm.entity.Participant;
import com.kartingrm.entity.Reservation;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender sender;

    public void sendReceipt(Reservation r, byte[] pdf){
        for (Participant p : r.getParticipantsList()){
            send(p.getEmail(), pdf);
        }
    }

    /* ---------- privado ---------- */
    private void send(String to, byte[] pdf){
        try{
            MimeMessage mime = sender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(mime, true);
            h.setTo(to);
            h.setSubject("Comprobante de Reserva KartingRM");
            h.setText("Adjunto encontrará su comprobante.");
            h.addAttachment("comprobante.pdf", new ByteArrayResource(pdf));
            sender.send(mime);
        }catch (Exception e){
            throw new RuntimeException("Error enviando email", e);
        }
    }
}

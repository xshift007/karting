package com.kartingrm.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service @RequiredArgsConstructor
public class MailService {
    private final JavaMailSender sender;

    public void sendReceipt(String to, byte[] pdf) {
        try{
            MimeMessage mime = sender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(mime, true);
            h.setTo(to); h.setSubject("Comprobante de Reserva KartingRM");
            h.setText("Adjunto encontrará su comprobante.");
            h.addAttachment("comprobante.pdf", new ByteArrayResource(pdf));
            sender.send(mime);
        }catch (Exception e){
            throw new RuntimeException("Error enviando email", e);
        }
    }
}

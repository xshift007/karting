package com.kartingrm.service;

import com.kartingrm.entity.Payment;
import com.kartingrm.entity.Reservation;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {
    public byte[] buildReceipt(Reservation r, Payment p) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("Comprobante de Reserva KartingRM", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            doc.add(new Paragraph("Código reserva: " + r.getReservationCode()));
            doc.add(new Paragraph("Cliente: " + r.getClient().getFullName()));
            doc.add(new Paragraph("Fecha/Hora sesión: " + r.getSession().getSessionDate() + " " + r.getSession().getStartTime()));
            doc.add(new Paragraph("Número de participantes: " + r.getParticipants()));
            doc.add(new Paragraph("Tarifa: " + r.getRateType()));
            doc.add(new Paragraph("Precio base: " + r.getBasePrice()));
            doc.add(new Paragraph("Descuento aplicado: " + r.getDiscountPercentage() + "%"));
            doc.add(new Paragraph("Precio final: " + r.getFinalPrice()));
            doc.add(new Paragraph("IVA: " + p.getVatAmount()));
            doc.add(new Paragraph("Total (incl. IVA): " + p.getFinalAmountInclVat()));
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}
package com.kartingrm.service;

import com.kartingrm.entity.*;
import com.kartingrm.service.pricing.DiscountService;
import com.lowagie.text.*;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final DiscountService discService;
    private final ClientService   clientService;

    private static Font font(int size, boolean bold){
        return FontFactory.getFont(FontFactory.HELVETICA, size,
                bold ? Font.BOLD : Font.NORMAL);
    }

    public byte[] buildReceipt(Reservation r, Payment p){

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            doc.add(new Paragraph("Comprobante de Reserva KartingRM", font(16,true)));
            doc.add(new Paragraph("Código reserva: " + r.getReservationCode()));
            doc.add(new Paragraph("Fecha/Hora sesión: " +
                    r.getSession().getSessionDate() + " " + r.getSession().getStartTime()));
            doc.add(new Paragraph(" "));

            /* -------- tabla por participante -------- */
            Table t = new Table(7);
            Stream.of("Cliente","Tarifa","Desc.Grupal","Desc.Frecuente",
                            "Desc.Cumple","IVA","Total")
                    .forEach(h -> {
                        Cell c = new Cell(new Phrase(h, font(9,true)));
                        t.addCell(c);
                    });

            double tarifaBase = r.getBasePrice();
            double dGrupo = discService.groupDiscount(r.getParticipants());

            double dFreq  = discService.frequentDiscount(
                    clientService.getTotalVisitsThisMonth(r.getClient()));

            for (Participant part : r.getParticipantsList()){
                double dCumple = part.isBirthday() ? 50 : 0;
                double subtotal = tarifaBase * (1 - (dGrupo+dFreq+dCumple)/100);
                double iva = subtotal * 0.19;
                double total = subtotal + iva;

                t.addCell(part.getFullName());
                t.addCell(String.valueOf((int)tarifaBase));
                t.addCell(dGrupo + "%");
                t.addCell(dFreq  + "%");
                t.addCell(dCumple+ "%");
                t.addCell(String.format("%.0f", iva));
                t.addCell(String.format("%.0f", total));
            }
            doc.add(t);

            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Precio final grupo: " + r.getFinalPrice()));
            doc.add(new Paragraph("IVA: " + p.getVatAmount()));
            doc.add(new Paragraph("Total (incl. IVA): " + p.getFinalAmountInclVat()));

            doc.close();
            return baos.toByteArray();

        }catch (Exception e){
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}

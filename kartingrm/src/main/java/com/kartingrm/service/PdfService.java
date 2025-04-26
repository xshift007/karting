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

    /* se elimina cálculo redundante:
       la tabla sigue mostrando el detalle, pero los descuentos
       se obtienen de Reservation → getDiscountPercentage().         */
    /** Helper para crear fonts de iText */
    private static Font font(int size, boolean bold) {
        return FontFactory.getFont(
                FontFactory.HELVETICA,
                size,
                bold ? Font.BOLD : Font.NORMAL
        );
    }


    public byte[] buildReceipt(Reservation r, Payment p) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4);   /* … sin cambios … */

            /* -------- tabla por participante -------- */
            Table t = new Table(6);
            Stream.of("Cliente","Tarifa","%Descuento",
                            "Subtotal","IVA","Total")
                    .forEach(h -> t.addCell(new Cell(new Phrase(h, font(9,true)))));

            double tarifaBase = r.getBasePrice();
            double pct        = r.getDiscountPercentage();

            for (Participant part : r.getParticipantsList()) {

                double subtotal = tarifaBase * (1 - pct/100);
                double iva      = subtotal * 0.19;
                double total    = subtotal + iva;

                t.addCell(part.getFullName());
                t.addCell(String.format("%.0f", tarifaBase));
                t.addCell(pct + "%");
                t.addCell(String.format("%.0f", subtotal));
                t.addCell(String.format("%.0f", iva));
                t.addCell(String.format("%.0f", total));
            }
            doc.add(t);

            /* resume */
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Precio final grupo: " + r.getFinalPrice()));
            doc.add(new Paragraph("IVA: " + p.getVatAmount()));
            doc.add(new Paragraph("Total (incl. IVA): " + p.getFinalAmountInclVat()));

            doc.close();
            return baos.toByteArray();
        }
        catch (Exception e) { throw new RuntimeException("Error generando PDF", e); }
    }
}

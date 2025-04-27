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

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);  // ① writer
            doc.open();                        // ② ¡abrir antes de add()!

            /* -------- tabla por participante -------- */
            Table t = new Table(6);
            Stream.of("Cliente","Tarifa","%Descuento",
                            "Subtotal","IVA","Total")
                    .forEach(h -> t.addCell(new Cell(
                            new Phrase(h, font(9,true)))));

            double precioUnitFinal = r.getFinalPrice() / r.getParticipants();
            double ivaUnit         = precioUnitFinal * 0.19 / 1.19;
            double netoUnit        = precioUnitFinal - ivaUnit;

            for (Participant part : r.getParticipantsList()) {
                t.addCell(part.getFullName());
                t.addCell(String.format("%.0f", r.getBasePrice()));
                t.addCell(String.format("%.1f %%", r.getDiscountPercentage()));
                t.addCell(String.format("%.0f", netoUnit));
                t.addCell(String.format("%.0f", ivaUnit));
                t.addCell(String.format("%.0f", precioUnitFinal));
            }

            doc.add(t);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Precio final grupo: " + r.getFinalPrice()));
            doc.add(new Paragraph("IVA: " + p.getVatAmount()));
            doc.add(new Paragraph("Total (incl. IVA): " + p.getFinalAmountInclVat()));

            doc.close();                       // ③ cerrar
            return baos.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}

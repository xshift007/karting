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

/**
 * Genera el recibo PDF con desglose de descuentos.
 */
@Service
@RequiredArgsConstructor
public class PdfService {

    private final DiscountService discSvc;
    private final ClientService   clientSvc;

    /* ---------- helpers ---------- */
    private static Font font(int size, boolean bold) {
        return FontFactory.getFont(
                FontFactory.HELVETICA, size,
                bold ? Font.BOLD : Font.NORMAL);
    }

    public byte[] buildReceipt(Reservation r, Payment p) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            /* ================================================================
             *  1. Desglose de descuentos
             * ============================================================= */
            int people       = r.getParticipants();
            long bdayCount   = r.getParticipantsList().stream()
                    .filter(Participant::isBirthday).count();
            int visits       = clientSvc.getTotalVisitsThisMonth(r.getClient());

            double dGroupPct = discSvc.groupDiscount(people);
            double dFreqPct  = discSvc.frequentDiscount(visits);
            double dBdayPct  = discSvc.birthdayDiscount(people, (int) bdayCount);

            Table d = new Table(2);
            d.setWidths(new int[]{2,1});
            Stream.of(new String[][] {
                    {"Grupo",       "%.1f %%".formatted(dGroupPct)},
                    {"Frecuente",   "%.1f %%".formatted(dFreqPct)},
                    {"Cumpleaños",  "%.1f %%".formatted(dBdayPct)},
                    {"TOTAL",       "%.1f %%".formatted(r.getDiscountPercentage())}
            }).forEach(row -> {
                d.addCell(new Phrase(row[0], font(9,false)));
                d.addCell(new Phrase(row[1], font(9,false)));
            });

            doc.add(new Paragraph("Descuentos aplicados:", font(10,true)));
            doc.add(d);
            doc.add(new Paragraph(" "));

            /* ================================================================
             *  2. Detalle por participante
             * ============================================================= */
            Table t = new Table(6);
            Stream.of("Cliente","Tarifa","%Descuento",
                            "Subtotal","IVA","Total")
                    .forEach(h -> t.addCell(new Cell(new Phrase(h, font(9,true)))));

            double unitFinal = r.getFinalPrice() / people;
            double ivaUnit   = unitFinal * 0.19 / 1.19;
            double netUnit   = unitFinal - ivaUnit;

            for (Participant part : r.getParticipantsList()) {
                t.addCell(part.getFullName());
                t.addCell(String.format("%.0f", r.getBasePrice()));
                t.addCell(String.format("%.1f %%", r.getDiscountPercentage()));
                t.addCell(String.format("%.0f", netUnit));
                t.addCell(String.format("%.0f", ivaUnit));
                t.addCell(String.format("%.0f", unitFinal));
            }

            doc.add(t);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Precio final grupo: " + r.getFinalPrice()));
            doc.add(new Paragraph("IVA: " + p.getVatAmount()));
            doc.add(new Paragraph("Total (incl. IVA): " + p.getFinalAmountInclVat()));

            doc.close();
            return baos.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}

package com.kartingrm.service;

import com.kartingrm.entity.*;
import com.kartingrm.service.pricing.DiscountService;
import com.lowagie.text.*;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Recibo con reglas:
 *   • Grupo → todos
 *   • Frecuente → sólo titular
 *   • 50 % → 1 – 2 cumpleañeros ganadores
 */
@Service
@RequiredArgsConstructor
public class PdfService {

    private final DiscountService disc;
    private final ClientService   clientSvc;

    private static Font f(int size, boolean bold) {
        return FontFactory.getFont(FontFactory.HELVETICA,
                size,
                bold ? Font.BOLD : Font.NORMAL);
    }

    /**
     * Genera el PDF del comprobante; el precio **total** que se muestra
     * YA incluye IVA.  Se desglosan Neto + IVA sólo a modo informativo
     * (no se vuelven a sumar).
     */
    public byte[] buildReceipt(Reservation r, Payment p) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            /* ---------- datos base ---------- */
            int     people   = r.getParticipants();
            Client  owner    = r.getClient();
            int     visits   = clientSvc.getTotalVisitsThisMonth(owner);

            double  gPct     = disc.groupDiscount(people);
            double  fPct     = disc.frequentDiscount(visits);

            List<Participant> list = new ArrayList<>(r.getParticipantsList());

            long   bdayCount = list.stream().filter(Participant::isBirthday).count();
            int    winners   = disc.birthdayWinners(people, (int) bdayCount);

            /* ---------- PDF ---------- */
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            /* ------ bloque cabecera descuentos ------ */
            Table head = new Table(2); head.setWidths(new int[]{2,1});
            Stream.of(new String[][]{
                    {"Grupo",      "%.0f %%".formatted(gPct)},
                    {"Frecuente",  "%.0f %%".formatted(fPct)},
                    {"Cumpleaños", winners > 0 ? "50,0 %" : "0,0 %"}
            }).forEach(row -> {
                head.addCell(new Phrase(row[0], f(9,false)));
                head.addCell(new Phrase(row[1], f(9,false)));
            });
            doc.add(new Paragraph("Descuentos aplicados:", f(10,true)));
            doc.add(head);
            doc.add(new Paragraph(" "));

            /* ---------- cálculo precio unitario ---------- */
            double afterGroup = r.getBasePrice() * (1 - gPct / 100);
            double ownerUnit  = afterGroup * (1 - fPct / 100);
            double regular    = afterGroup;

            /* ---------- ganadores 50 % cumpleaños ---------- */
            Map<Long,Boolean> winnerMap = new HashMap<>();
            int winnersLeft = winners;

            Participant ownerPart = list.stream()
                    .filter(pt -> pt.getEmail().equalsIgnoreCase(owner.getEmail()))
                    .findFirst()
                    .orElse(null);

            boolean ownerIsBirthday = ownerPart != null && ownerPart.isBirthday();
            if (ownerIsBirthday && winnersLeft > 0) {
                winnerMap.put(ownerPart.getId(), true);
                winnersLeft--;
            }
            for (Participant pt : list) {
                if (winnersLeft == 0) break;
                if (pt.isBirthday() && !winnerMap.containsKey(pt.getId())) {
                    winnerMap.put(pt.getId(), true);
                    winnersLeft--;
                }
            }

            /* ---------- detalle por participante ---------- */
            Table table = new Table(6);
            Stream.of("Cliente", "Tarifa", "%Descuento",
                            "Subtotal", "IVA", "Total c/IVA")
                    .forEach(h ->
                            table.addCell(new Cell(new Phrase(h, f(9,true)))));

            double total = 0;
            for (Participant pt : list) {

                boolean isOwner  = pt == ownerPart;
                boolean isWinner = Boolean.TRUE.equals(winnerMap.get(pt.getId()));

                double unit = isOwner ? ownerUnit : regular;
                String pct  = "%.0f".formatted(gPct);         // grupo

                if (isOwner)  pct += " ; %.0f".formatted(fPct); // frecuente
                if (isWinner) { unit *= 0.5; pct += " ; 50"; }  // cumpleaños
                pct += " %";

                double iva = unit * 0.19 / 1.19;   // IVA incluido
                double net = unit - iva;
                total += unit;

                table.addCell(pt.getFullName());
                table.addCell(String.format("%d", Math.round(r.getBasePrice())));
                table.addCell(pct);
                table.addCell(String.format("%d", Math.round(net)));
                table.addCell(String.format("%d", Math.round(iva)));
                table.addCell(String.format("%d", Math.round(unit)));
            }

            doc.add(table);
            doc.add(new Paragraph(" "));

            /* ---------- resumen final ---------- */
            double vat  = total * 0.19 / 1.19;
            double netto = total - vat;

            doc.add(new Paragraph("Subtotal (sin IVA): %.0f".formatted(netto)));
            doc.add(new Paragraph("IVA (19 %%): %.0f".formatted(vat)));
            doc.add(new Paragraph("Precio final grupo (incl. IVA): %.0f"
                    .formatted(total)));

            doc.close();
            return baos.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}

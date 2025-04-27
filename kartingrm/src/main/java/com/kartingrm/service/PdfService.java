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
                size, bold ? Font.BOLD : Font.NORMAL);
    }

    public byte[] buildReceipt(Reservation r, Payment p) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            /* ---------- datos ---------- */
            int     people   = r.getParticipants();
            Client  owner    = r.getClient();
            int     visits   = clientSvc.getTotalVisitsThisMonth(owner);

            double  gPct     = disc.groupDiscount(people);
            double  fPct     = disc.frequentDiscount(visits);

            List<Participant> list = new ArrayList<>(r.getParticipantsList());

            long bdayCount   = list.stream().filter(Participant::isBirthday).count();
            int  winners     = disc.birthdayWinners(people, (int) bdayCount);

            /* ---------- PDF ---------- */
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            /* cabecera descuento */
            Table head = new Table(2); head.setWidths(new int[]{2,1});
            Stream.of(new String[][] {
                    {"Grupo",     "%.0f %%".formatted(gPct)},
                    {"Frecuente", "%.0f %%".formatted(fPct)},
                    {"Cumpleaños", winners > 0 ? "50,0 %" : "0,0 %"}
            }).forEach(row -> {
                head.addCell(new Phrase(row[0], f(9,false)));
                head.addCell(new Phrase(row[1], f(9,false)));
            });
            doc.add(new Paragraph("Descuentos aplicados:", f(10,true)));
            doc.add(head);
            doc.add(new Paragraph(" "));

            /* precios tras grupo */
            double afterGroup = r.getBasePrice() * (1 - gPct / 100);
            double ownerUnit  = afterGroup * (1 - fPct / 100);
            double regular    = afterGroup;

            /* determinar ganadores 50 % */
            Map<Long,Boolean> winnerMap = new HashMap<>();
            int winnersLeft = winners;

            Participant ownerPart = list.stream()
                    .filter(p -> p.getEmail().equalsIgnoreCase(owner.getEmail()))
                    .findFirst().orElse(null);

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

            /* tabla detalle */
            Table table = new Table(6);
            Stream.of("Cliente","Tarifa","%Descuento",
                            "Subtotal","IVA","Total")
                    .forEach(h -> table.addCell(
                            new Cell(new Phrase(h, f(9,true)))));

            double total = 0;
            for (Participant pt : list) {

                boolean isOwner  = pt == ownerPart;
                boolean isWinner = Boolean.TRUE.equals(winnerMap.get(pt.getId()));

                double unit = isOwner ? ownerUnit : regular;
                String pct  = "%.0f".formatted(gPct);

                if (isOwner)  pct += " ; %.0f".formatted(fPct);
                if (isWinner) { unit *= 0.5; pct += " ; 50"; }
                pct += " %";

                double iva = unit * 0.19 / 1.19;
                double net = unit - iva;
                total += unit;

                table.addCell(pt.getFullName());
                table.addCell(String.format("%.0f", r.getBasePrice()));
                table.addCell(pct);
                table.addCell(String.format("%.0f", Math.round(net)));
                table.addCell(String.format("%.0f", Math.round(iva)));
                table.addCell(String.format("%.0f", Math.round(unit)));
            }

            doc.add(table); doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Precio final grupo: %.0f".formatted(total)));
            doc.add(new Paragraph("IVA: %.0f".formatted(p.getVatAmount())));
            doc.add(new Paragraph("Total (incl. IVA): %.0f".formatted(p.getFinalAmountInclVat())));

            doc.close();
            return baos.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}

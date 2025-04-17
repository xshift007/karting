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

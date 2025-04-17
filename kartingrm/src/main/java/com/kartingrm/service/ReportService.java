package com.kartingrm.service;

import com.kartingrm.dto.IncomeByGroupDTO;
import com.kartingrm.dto.IncomeByRateDTO;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final EntityManager em;

    public ReportService(EntityManager em) { this.em = em; }

    public List<IncomeByRateDTO> ingresosPorTarifa(LocalDate from, LocalDate to) {
        return em.createQuery("""
            SELECT new com.kartingrm.dto.IncomeByRateDTO(r.rateType,
                       SUM(p.finalAmountInclVat))
            FROM Payment p JOIN p.reservation r
            WHERE p.paymentDate BETWEEN :f AND :t
            GROUP BY r.rateType
        """, IncomeByRateDTO.class)
                .setParameter("f", from.atStartOfDay())
                .setParameter("t", to.atTime(23,59,59))
                .getResultList();
    }

    public List<IncomeByGroupDTO> ingresosPorGrupo(LocalDate f, LocalDate t) {
        // similar: agrupar por r.participants rangos (1‑2, 3‑5…)
    }
}

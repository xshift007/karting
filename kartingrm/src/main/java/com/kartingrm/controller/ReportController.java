package com.kartingrm.controller;

import com.kartingrm.dto.IncomeByGroupDTO;
import com.kartingrm.dto.IncomeByRateDTO;
import com.kartingrm.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reports;

    @GetMapping("/by-rate")
    public List<IncomeByRateDTO> byRate(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to){
        return reports.ingresosPorTarifa(from, to);
    }

    @GetMapping("/by-group")
    public List<IncomeByGroupDTO> byGroup(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to){
        return reports.ingresosPorGrupo(from, to);
    }
}

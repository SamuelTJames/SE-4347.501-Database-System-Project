package com.se4347.database_system_project.api.rest;

import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.service.AircraftUtilizationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final AircraftUtilizationService aircraftUtilizationService;

    public ReportController(AircraftUtilizationService aircraftUtilizationService) {
        this.aircraftUtilizationService = aircraftUtilizationService;
    }

    @GetMapping("/aircraft-utilization")
    public List<AircraftUtilization> aircraftUtilization(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return aircraftUtilizationService.getUtilizationReport(start, end);
    }
}

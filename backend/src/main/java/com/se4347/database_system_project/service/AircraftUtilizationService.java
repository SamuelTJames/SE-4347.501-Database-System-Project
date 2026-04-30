package com.se4347.database_system_project.service;

import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.dao.jpa.AirplaneRepository;
import com.se4347.database_system_project.exception.InvalidInputException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AircraftUtilizationService {

    private final AirplaneRepository airplaneRepository;

    public AircraftUtilizationService(AirplaneRepository airplaneRepository) {
        this.airplaneRepository = airplaneRepository;
    }

    /**
     * For a given date range, return each airplane (registration + type) and the total number
     * of leg instances assigned to it. Airplanes with zero assigned flights are included.
     */
    public List<AircraftUtilization> getUtilizationReport(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new InvalidInputException("Start and end dates are required.");
        }
        if (end.isBefore(start)) {
            throw new InvalidInputException("End date must be on or after start date.");
        }
        return airplaneRepository.findUtilizationReport(start, end).stream()
                .sorted(Comparator
                        .comparingLong(AircraftUtilization::totalFlights).reversed()
                        .thenComparing(AircraftUtilization::airplaneId))
                .toList();
    }
}

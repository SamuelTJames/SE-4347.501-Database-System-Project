package com.se4347.database_system_project.service;

import com.se4347.database_system_project.dao.jpa.AirplaneRepository;
import com.se4347.database_system_project.dao.jpa.LegInstanceRepository;
import org.springframework.stereotype.Service;

@Service
public class AircraftUtilizationService {

    private final AirplaneRepository airplaneRepository;
    private final LegInstanceRepository legInstanceRepository;

    public AircraftUtilizationService(AirplaneRepository airplaneRepository,
                                      LegInstanceRepository legInstanceRepository) {
        this.airplaneRepository = airplaneRepository;
        this.legInstanceRepository = legInstanceRepository;
    }

    // TODO: getUtilizationReport
}

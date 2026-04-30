package com.se4347.database_system_project.service;

import com.se4347.database_system_project.dao.jpa.FareRepository;
import com.se4347.database_system_project.dao.jpa.FlightLegRepository;
import com.se4347.database_system_project.dao.jpa.FlightRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightQueryService {

    private final FlightRepository flightRepository;
    private final FlightLegRepository flightLegRepository;
    private final FareRepository fareRepository;

    public FlightQueryService(FlightRepository flightRepository,
                              FlightLegRepository flightLegRepository,
                              FareRepository fareRepository) {
        this.flightRepository = flightRepository;
        this.flightLegRepository = flightLegRepository;
        this.fareRepository = fareRepository;
    }

    // TODO: getFlightByNumber
}

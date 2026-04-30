package com.se4347.database_system_project.service;

import com.se4347.database_system_project.dao.jpa.AirportRepository;
import com.se4347.database_system_project.dao.jpa.FlightLegRepository;
import org.springframework.stereotype.Service;

@Service
public class ItineraryService {

    private final AirportRepository airportRepository;
    private final FlightLegRepository flightLegRepository;

    public ItineraryService(AirportRepository airportRepository, FlightLegRepository flightLegRepository) {
        this.airportRepository = airportRepository;
        this.flightLegRepository = flightLegRepository;
    }

    // TODO: searchByCity / searchByCode
    // TODO: findDirectFlights
    // TODO: findOneStopFlights
}

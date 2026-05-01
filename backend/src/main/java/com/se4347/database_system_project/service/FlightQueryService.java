package com.se4347.database_system_project.service;

import com.se4347.database_system_project.api.dto.AirportSummary;
import com.se4347.database_system_project.api.dto.FareSummary;
import com.se4347.database_system_project.api.dto.FlightDetails;
import com.se4347.database_system_project.api.dto.FlightLegSummary;
import com.se4347.database_system_project.dao.jpa.FareRepository;
import com.se4347.database_system_project.dao.jpa.FlightLegRepository;
import com.se4347.database_system_project.dao.jpa.FlightRepository;
import com.se4347.database_system_project.domain.Airport;
import com.se4347.database_system_project.domain.Fare;
import com.se4347.database_system_project.domain.Flight;
import com.se4347.database_system_project.domain.FlightLeg;
import com.se4347.database_system_project.exception.InvalidInputException;
import com.se4347.database_system_project.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
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

    public FlightDetails getFlightByNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.isBlank()) {
            throw new InvalidInputException("Flight number is required.");
        }
        String normalized = flightNumber.trim().toUpperCase();
        Flight flight = flightRepository.findById(normalized)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + normalized));

        List<FlightLegSummary> legs = flightLegRepository.findByFlightNumber(normalized).stream()
                .map(FlightQueryService::toLegSummary)
                .toList();

        List<FareSummary> fares = fareRepository.findByFlightNumber(normalized).stream()
                .map(FlightQueryService::toFareSummary)
                .toList();

        return new FlightDetails(flight.getNumber(), flight.getAirline(), flight.getWeekdays(), legs, fares);
    }

    private static FlightLegSummary toLegSummary(FlightLeg leg) {
        return new FlightLegSummary(
                leg.getId().getLegNo(),
                toAirportSummary(leg.getDepAirport()),
                toAirportSummary(leg.getArrAirport()),
                leg.getScheduledDepTime(),
                leg.getScheduledArrTime());
    }

    private static FareSummary toFareSummary(Fare fare) {
        return new FareSummary(fare.getId().getCode(), fare.getAmount(), fare.getRestriction());
    }

    static AirportSummary toAirportSummary(Airport airport) {
        return new AirportSummary(airport.getAirportCode(), airport.getName(),
                airport.getCity(), airport.getState());
    }
}

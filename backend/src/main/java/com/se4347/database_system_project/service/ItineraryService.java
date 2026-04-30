package com.se4347.database_system_project.service;

import com.se4347.database_system_project.api.dto.DirectItinerary;
import com.se4347.database_system_project.api.dto.ItineraryResults;
import com.se4347.database_system_project.api.dto.OneStopItinerary;
import com.se4347.database_system_project.dao.jpa.AirportRepository;
import com.se4347.database_system_project.dao.jpa.FlightLegRepository;
import com.se4347.database_system_project.domain.Airport;
import com.se4347.database_system_project.domain.FlightLeg;
import com.se4347.database_system_project.exception.InvalidInputException;
import com.se4347.database_system_project.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItineraryService {

    private final AirportRepository airportRepository;
    private final FlightLegRepository flightLegRepository;

    public ItineraryService(AirportRepository airportRepository, FlightLegRepository flightLegRepository) {
        this.airportRepository = airportRepository;
        this.flightLegRepository = flightLegRepository;
    }

    /**
     * Resolve origin/destination (each may be a 3-letter airport code or a city name) and return
     * direct + one-connection itineraries between them.
     */
    public ItineraryResults findItineraries(String origin, String destination) {
        Set<String> originCodes = resolve(origin, "origin");
        Set<String> destinationCodes = resolve(destination, "destination");

        if (originCodes.equals(destinationCodes)) {
            throw new InvalidInputException("Origin and destination must differ.");
        }

        List<DirectItinerary> direct = flightLegRepository
                .findDirectLegs(originCodes, destinationCodes).stream()
                .map(ItineraryService::toDirect)
                .toList();

        List<OneStopItinerary> oneStop = flightLegRepository
                .findOneStopLegPairs(originCodes, destinationCodes).stream()
                .map(row -> {
                    FlightLeg first = (FlightLeg) row[0];
                    FlightLeg second = (FlightLeg) row[1];
                    return new OneStopItinerary(toDirect(first), toDirect(second),
                            FlightQueryService.toAirportSummary(first.getArrAirport()));
                })
                .toList();

        return new ItineraryResults(direct, oneStop);
    }

    private Set<String> resolve(String input, String label) {
        if (input == null || input.isBlank()) {
            throw new InvalidInputException(label + " is required.");
        }
        String trimmed = input.trim();

        // Treat 3-character input as an airport code first; fall through to city if not found.
        if (trimmed.length() == 3) {
            var byCode = airportRepository.findByCodeIgnoreCase(trimmed);
            if (byCode.isPresent()) {
                return Set.of(byCode.get().getAirportCode());
            }
        }

        List<Airport> byCity = airportRepository.findByCityIgnoreCase(trimmed);
        if (byCity.isEmpty()) {
            throw new NotFoundException("No airport matched " + label + " '" + trimmed + "'.");
        }
        return byCity.stream().map(Airport::getAirportCode).collect(Collectors.toUnmodifiableSet());
    }

    private static DirectItinerary toDirect(FlightLeg leg) {
        return new DirectItinerary(
                leg.getId().getFlightNumber(),
                leg.getFlight().getAirline(),
                leg.getId().getLegNo(),
                FlightQueryService.toAirportSummary(leg.getDepAirport()),
                FlightQueryService.toAirportSummary(leg.getArrAirport()),
                leg.getScheduledDepTime(),
                leg.getScheduledArrTime());
    }
}

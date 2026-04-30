package com.se4347.database_system_project.api.rest;

import com.se4347.database_system_project.api.dto.FlightDetails;
import com.se4347.database_system_project.api.dto.ItineraryResults;
import com.se4347.database_system_project.service.FlightQueryService;
import com.se4347.database_system_project.service.ItineraryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightQueryService flightQueryService;
    private final ItineraryService itineraryService;

    public FlightController(FlightQueryService flightQueryService,
                            ItineraryService itineraryService) {
        this.flightQueryService = flightQueryService;
        this.itineraryService = itineraryService;
    }

    @GetMapping("/{number}")
    public FlightDetails getFlight(@PathVariable String number) {
        return flightQueryService.getFlightByNumber(number);
    }

    @GetMapping("/trip")
    public ItineraryResults trip(@RequestParam("from") String from,
                                 @RequestParam("to") String to) {
        return itineraryService.findItineraries(from, to);
    }
}

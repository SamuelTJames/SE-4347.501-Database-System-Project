package com.se4347.database_system_project.ui;

import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.api.dto.DirectItinerary;
import com.se4347.database_system_project.api.dto.FlightDetails;
import com.se4347.database_system_project.api.dto.ItineraryResults;
import com.se4347.database_system_project.api.dto.OneStopItinerary;
import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.service.AircraftUtilizationService;
import com.se4347.database_system_project.service.BookingService;
import com.se4347.database_system_project.service.FlightQueryService;
import com.se4347.database_system_project.service.ItineraryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MVQueries
{
	private final FlightQueryService flightQueryService;
    private final ItineraryService itineraryService;
    private final BookingService bookingService;
    private final AircraftUtilizationService aircraftUtilizationService;

    public MVQueries(FlightQueryService flightQueryService,
	                   ItineraryService itineraryService,
	                   BookingService bookingService,
	                   AircraftUtilizationService aircraftUtilizationService) 
    {
        this.flightQueryService = flightQueryService;
        this.itineraryService = itineraryService;
        this.bookingService = bookingService;
        this.aircraftUtilizationService = aircraftUtilizationService;
    }
    
    public String availability(List<String> userInput)
    {
    	String display;
    	
    	List<SeatAvailability> output = bookingService.checkSeatAvailability(userInput.get(0), LocalDate.parse(userInput.get(1)));
    	
    	display = "Flight: " + 
    				userInput.get(0) + 
    				"\nSeats Remaining: " + 
    				String.valueOf(output.get(0).remainingSeats()); 
    	
    	return display;
    }
    
}

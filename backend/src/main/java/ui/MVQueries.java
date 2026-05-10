package com.se4347.database_system_project.ui;

import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.api.dto.DirectItinerary;
import com.se4347.database_system_project.api.dto.FlightDetails;
import com.se4347.database_system_project.api.dto.ItineraryResults;
import com.se4347.database_system_project.api.dto.OneStopItinerary;
import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.api.dto.FlightLegSummary;
import com.se4347.database_system_project.api.dto.BookingConfirmation;
import com.se4347.database_system_project.dao.jpa.LegInstanceRepository;
import com.se4347.database_system_project.service.AircraftUtilizationService;
import com.se4347.database_system_project.service.BookingService;
import com.se4347.database_system_project.service.FlightQueryService;
import com.se4347.database_system_project.service.ItineraryService;
import com.se4347.database_system_project.exception.InvalidInputException;
import com.se4347.database_system_project.exception.NotFoundException;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.Duration;
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
    private final LegInstanceRepository legInstanceRepository;

    public MVQueries(FlightQueryService flightQueryService,
	                   ItineraryService itineraryService,
	                   BookingService bookingService,
	                   AircraftUtilizationService aircraftUtilizationService,
	                   LegInstanceRepository legInstanceRepository) 
    {
        this.flightQueryService = flightQueryService;
        this.itineraryService = itineraryService;
        this.bookingService = bookingService;
        this.aircraftUtilizationService = aircraftUtilizationService;
        this.legInstanceRepository = legInstanceRepository;
    }
    
    //2a Flight details from two airport codes and date
     public List<String> airportFlightSearch(List<String> userInput)
     {
    	 List<String> display = new ArrayList<String>();
    	 String directFlights = "******************\n*Direct Flights*\n******************\n",
    			 oneStopFlights = "*********************\n*One Stop Fligths*\n*********************\n";
    	 
    	 try
    	 {
	    	 ItineraryResults ir = itineraryService.findItineraries(userInput.get(0), userInput.get(1));
	    	 
	    	 LocalDate userDate = LocalDate.parse(userInput.get(2));
	    	 for(DirectItinerary di : ir.direct())
	    	 {
	    		 List<LocalDate> possDates = legInstanceRepository.findDatesByFlightNumber(di.flightNumber());
	    		 for(LocalDate i : possDates)
	    			 if(i.equals(userDate))
	    			 	directFlights = directFlights +
										"Airline: " +
										di.airline() +
										"\nFlight: " +
										di.flightNumber() +
										"\nDate: " +
										userInput.get(2) +
										"\nDeparture: " +
										String.valueOf(di.scheduledDepTime()) +
										"\nArrival: " +
										String.valueOf(di.scheduledArrTime())+
										"\n---------------------------------------------------------\n";
	    	 }
	    	 
	    	 for(OneStopItinerary osi : ir.oneStop())
	    	 {
	    		 DirectItinerary flightOne = osi.firstLeg();
	    		 DirectItinerary flightTwo = osi.secondLeg();
	    		 List<LocalDate> possDates = legInstanceRepository.findDatesByFlightNumber(flightOne.flightNumber());
	    		 for(LocalDate i : possDates)
	    		 	if(((int) Math.abs(Duration.between(flightOne.scheduledDepTime(), flightTwo.scheduledDepTime()).toMinutes())) >= 60 && i.equals(userDate))
	    			 	oneStopFlights = oneStopFlights +
										"Flight 1 Airline: " +
										flightOne.airline() +
										"\nFlight: " +
										flightOne.flightNumber() +
										"\nDate: " +
										userInput.get(2) +
										"\nDeparture: " +
										String.valueOf(flightOne.scheduledDepTime()) +
										"\nArrival: " +
										String.valueOf(flightOne.scheduledArrTime())+
										"\n\nFlight 2 Airline: " +
										flightTwo.airline() +
										"\nFlight: " +
										flightTwo.flightNumber() +
										"\nDate: " +
										userInput.get(2) +
										"\nDeparture: " +
										String.valueOf(flightTwo.scheduledDepTime()) +
										"\nArrival: " +
										String.valueOf(flightTwo.scheduledArrTime())+
										"\n---------------------------------------------------------\n";
	    	}
	    		
			 display.add(directFlights);
			 display.add(oneStopFlights);
    	}
		catch(NotFoundException | InvalidInputException e)
		{
			display.add("Error: ");
			display.add(e.getMessage());
		}
    	 
    	return display;
     }
    
    //2b Flight deatils from flight number and date
    public String numFlightSearch(List<String> userInput)
    {
    	String display = "Error: Flight with given date not found";
    	
    	try
    	{
	    	FlightDetails fd = flightQueryService.getFlightByNumber(userInput.get(0));
	    	List<LocalDate> possDates = legInstanceRepository.findDatesByFlightNumber(userInput.get(0));
	    	
	    	FlightLegSummary l = fd.legs().get(0);
	    	LocalDate userDate = LocalDate.parse(userInput.get(1));
	    	for(LocalDate i : possDates)
	    		if(i.equals(userDate)) {
	    			display = "Airline: " +
	    					  fd.airline() +
	    					  "\nFlight: " +
	    					  userInput.get(0) +
	    					  "\nDate: " +
	    					  userInput.get(1) +		//Change date design later
	    					  "\nDeparture: " +
	    					  String.valueOf(l.scheduledDepTime()) +
	    					  "\nArrival: " +
	    					  String.valueOf(l.scheduledArrTime());
	    			break;
	    		}
    	}
    	catch(NotFoundException | InvalidInputException e)
    	{
    		display = "Error: " + e.getMessage();
    	}
    	
    	return display;
    }
    
    //3a Aircraft Utilization Report from airplane registration number and time period (2 dates)
    public String aircraftUtilizationReport(List<String> userInput)
    {
    	String display;
    	
    	int index = 0, count = 0;
    	
    	try
    	{
	    	List<AircraftUtilization> airUtiList = aircraftUtilizationService.getUtilizationReport(
	                LocalDate.parse(userInput.get(1)), LocalDate.parse(userInput.get(2)));
	    	
	    	for(AircraftUtilization a : airUtiList)
	    	{
	    		if(a.airplaneId().equals(userInput.get(0)))
	    		{
	    			index = count;
	    			break;
	    		}
	    		else
	    		{
	    			count++;
	    		}
	    	}
	    	
	    	AircraftUtilization au = airUtiList.get(index);
	    	
	    	display =   //"Airplane: " +
	    				//Add Airplane name output here
	    				"\nType: " +
	    				au.airplaneType() +
	    				"\nRegistration Number: " +
	    				userInput.get(0) +
	    				"\nNumber of Flights: " +
	    				String.valueOf(au.totalFlights());
    	}
    	catch(DateTimeParseException | InvalidInputException e)
    	{
    		display = "Error: " + e.getMessage();
    	}
    	
    	return display;
    }
    
    //4b Check Seat Availability from flight number and date
    public String seatAvailability(List<String> userInput)
    {
    	String display;
    	
    	try
    	{
		List<SeatAvailability> sa = bookingService.checkSeatAvailability(userInput.get(0), LocalDate.parse(userInput.get(1)));
		
    	display = "Seats Remaining: " + 
    				String.valueOf(sa.get(0).remainingSeats());
    	}
    	catch(NotFoundException | InvalidInputException e)
    	{
    		display = "Error: " + e.getMessage();
    	}
    	
    	return display;
    }
    
    //4c Book a Seat with flight number, date, seat id, name, and phone number
    public String bookASeat(List<String> userInput)
    {
    	String display = "Seat Booked";
    		
    	try
    	{
    		FlightDetails fd = flightQueryService.getFlightByNumber(userInput.get(0));
        	
        	FlightLegSummary l = fd.legs().get(0);
    		
    		bookingService.bookSeat(userInput.get(0),					//Flight number
    								LocalDate.parse(userInput.get(1)),	//LocalDate (Change later?)
    								l.legNo(),							//Leg number
    								userInput.get(2),					//Seat
    								userInput.get(3),					//Name
    								userInput.get(4));					//Phone number
    	}
    	catch(NotFoundException | InvalidInputException | DateTimeParseException e)
    	{
    		display = "Error: " + e.getMessage();
    	}
    
    	return display;
    }
    
    //4d Passenger Itinerary Retrieval
    public String passengerItineraryRetrieval(String userInput)
    {
    	String display = "";
    	
    	List<PassengerItineraryEntry> pasItiEntry;
    	
    	try
    	{
    		if(userInput.charAt(0) >= 48 && userInput.charAt(0) <= 57)
    		{
    			pasItiEntry = bookingService.getPassengerItinerary(null, userInput);
    		}
    		else
    		{
    			pasItiEntry = bookingService.getPassengerItinerary(userInput, null);
    		}
	    	 
	    	for(PassengerItineraryEntry pie : pasItiEntry)
	     	{
	    		 display = 	display +
	    				 	"Leg: " +
				 			String.valueOf(pie.legNo()) +
				 			"\nStart: " +
				 			pie.depAirportCode() +
				 			"\nEnd: " +
				 			pie.arrAirportCode() +
				 			"\nDeparture: " +
				 			String.valueOf(pie.scheduledDepTime()) +
				 			"\nArrival: " +
				 			String.valueOf(pie.scheduledArrTime()) +
				 			"\nSeat Number: " +
				 			pie.seatNumber() +
				 			"\n-----------------------------------------------------------\n";
	     	}
    	}
    	catch(NotFoundException | InvalidInputException e)
     	{
     		display = "Error: " + e.getMessage();
     	}
    	
    	return display;
    }
}

package com.se4347.database_system_project.ui;

import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.api.dto.DirectItinerary;
import com.se4347.database_system_project.api.dto.FlightDetails;
import com.se4347.database_system_project.api.dto.ItineraryResults;
import com.se4347.database_system_project.api.dto.OneStopItinerary;
import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.api.dto.FlightLegSummary;
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
    
    //2a Flight details from two airport codes and date
     public List<String> airportFlightSearch(List<String> userInput)
     {
    	 List<String> display = new ArrayList<String>();
    	 String directFlights = "******************\n*Direct Flights*\n******************\n",
    			 oneStopFlights = "*********************\n*One Stop Fligths*\n*********************\n";
    	 
    	 ItineraryResults ir = itineraryService.findItineraries(userInput.get(0), userInput.get(1));
    	 
    	 //ADD FILTER USING GIVEN DATE
    	 
    	 for(DirectItinerary di : ir.direct())
    	 {
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
    	 
    	 return display;
     }
    
    //2b Flight deatils from flight number and date
    public String numFlightSearch(List<String> userInput)
    {
    	String display;
    	
    	FlightDetails fd = flightQueryService.getFlightByNumber(userInput.get(0));
    	
    	FlightLegSummary l = fd.legs().get(0);
    	
		display = "Airline: " +
					fd.airline() +
					"\nFlight: " +
					userInput.get(0) +
					"\nDate: " +
					userInput.get(1) +
					"\nDeparture: " +
					String.valueOf(l.scheduledDepTime()) +
					"\nArrival: " +
					String.valueOf(l.scheduledArrTime());
    	
    	return display;
    }
    
    //3a Aircraft Utilization Report from airplane registration number and time period (2 dates)
    public String aircraftUtilizationReport(List<String> userInput)
    {
    	String display;
    	
    	int index = 0, count = 0;
    	
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
    	
    	display = "Airplane: " +
    				
    				"\nType: " +
    				au.airplaneType() +
    				"\nRegistration Number: " +
    				userInput.get(0) +
    				"\nNumber of Flights: " +
    				String.valueOf(au.totalFlights());
    	
    	return display;
    }
    
    //4b Check Seat Availability from flight number and date
    public String seatAvailability(List<String> userInput)
    {
    	String display;
    	
		List<SeatAvailability> sa = bookingService.checkSeatAvailability(userInput.get(0), LocalDate.parse(userInput.get(1)));
		
    	display = "Seats Remaining: " + 
    				String.valueOf(sa.get(0).remainingSeats());
    	
    	return display;
    }
    
    //4d Passenger Itinerary Retrieval
    public String passengerItineraryRetrieval(String userInput)
    {
    	String display = "";
    	
    	 List<PassengerItineraryEntry> pasItiEntry = bookingService.getPassengerItinerary(userInput, null);
    	 
    	 
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
    	 
    	 return display;
    }
}

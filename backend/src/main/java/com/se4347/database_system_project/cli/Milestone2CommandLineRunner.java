package com.se4347.database_system_project.cli;

import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.api.dto.DirectItinerary;
import com.se4347.database_system_project.api.dto.FlightDetails;
import com.se4347.database_system_project.api.dto.ItineraryResults;
import com.se4347.database_system_project.api.dto.OneStopItinerary;
import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.exception.InvalidInputException;
import com.se4347.database_system_project.exception.NotFoundException;
import com.se4347.database_system_project.service.AircraftUtilizationService;
import com.se4347.database_system_project.service.BookingService;
import com.se4347.database_system_project.service.FlightQueryService;
import com.se4347.database_system_project.service.ItineraryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class Milestone2CommandLineRunner implements ApplicationRunner {

    private final FlightQueryService flightQueryService;
    private final ItineraryService itineraryService;
    private final BookingService bookingService;
    private final AircraftUtilizationService aircraftUtilizationService;

    public Milestone2CommandLineRunner(FlightQueryService flightQueryService,
                                       ItineraryService itineraryService,
                                       BookingService bookingService,
                                       AircraftUtilizationService aircraftUtilizationService) {
        this.flightQueryService = flightQueryService;
        this.itineraryService = itineraryService;
        this.bookingService = bookingService;
        this.aircraftUtilizationService = aircraftUtilizationService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("cli")) {
            return;
        }
        List<String> values = args.getOptionValues("cli");
        if (values == null || values.isEmpty() || values.get(0).isBlank()) {
            printUsage();
            return;
        }
        String command = values.get(0).trim().toLowerCase();
        List<String> positional = args.getNonOptionArgs();

        try {
            switch (command) {
                case "flight" -> runFlight(positional);
                case "trip" -> runTrip(positional);
                case "availability" -> runAvailability(positional);
                case "passenger" -> runPassenger(args);
                case "utilization" -> runUtilization(positional);
                default -> printUsage();
            }
        } catch (NotFoundException | InvalidInputException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void runFlight(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Usage: --cli flight <FLIGHT_NUMBER>");
            return;
        }
        FlightDetails details = flightQueryService.getFlightByNumber(args.get(0));
        System.out.println("Flight " + details.number() + " (" + details.airline() + ")  weekdays=" + details.weekdays());
        details.legs().forEach(l -> System.out.println(
                "  Leg " + l.legNo() + ": " + l.depAirport().airportCode() + " -> " + l.arrAirport().airportCode()
                        + "  " + l.scheduledDepTime() + " -> " + l.scheduledArrTime()));
        if (details.fares().isEmpty()) {
            System.out.println("  (no fares listed)");
        } else {
            details.fares().forEach(f -> System.out.println(
                    "  Fare " + f.code() + ": $" + f.amount()
                            + Optional.ofNullable(f.restriction()).map(r -> " (" + r + ")").orElse("")));
        }
    }

    private void runTrip(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Usage: --cli trip <ORIGIN> <DESTINATION>   (airport code or city)");
            return;
        }
        ItineraryResults results = itineraryService.findItineraries(args.get(0), args.get(1));
        System.out.println("Direct flights (" + results.direct().size() + "):");
        for (DirectItinerary d : results.direct()) {
            System.out.println("  " + d.flightNumber() + " leg " + d.legNo() + "  "
                    + d.origin().airportCode() + " " + d.scheduledDepTime()
                    + " -> " + d.destination().airportCode() + " " + d.scheduledArrTime());
        }
        System.out.println("One-connection itineraries (" + results.oneStop().size() + "):");
        for (OneStopItinerary o : results.oneStop()) {
            System.out.println("  " + o.firstLeg().flightNumber() + " " + o.firstLeg().origin().airportCode()
                    + " " + o.firstLeg().scheduledDepTime() + " -> " + o.connection().airportCode()
                    + " " + o.firstLeg().scheduledArrTime() + "  ||  " + o.secondLeg().flightNumber()
                    + " " + o.connection().airportCode() + " " + o.secondLeg().scheduledDepTime()
                    + " -> " + o.secondLeg().destination().airportCode() + " " + o.secondLeg().scheduledArrTime());
        }
    }

    private void runAvailability(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Usage: --cli availability <FLIGHT_NUMBER> <YYYY-MM-DD>");
            return;
        }
        List<SeatAvailability> rows = bookingService.checkSeatAvailability(args.get(0), LocalDate.parse(args.get(1)));
        for (SeatAvailability r : rows) {
            System.out.println(r.flightNumber() + " leg " + r.legNo() + " on " + r.date()
                    + " | airplane " + r.airplaneId()
                    + " | capacity " + r.airplaneCapacity()
                    + " | confirmed " + r.confirmedReservations()
                    + " | remaining " + r.remainingSeats());
        }
    }

    private void runPassenger(ApplicationArguments args) {
        String name = singleOption(args, "name");
        String phone = singleOption(args, "phone");
        if (name == null && phone == null) {
            System.out.println("Usage: --cli passenger --name=\"Jane Smith\"   OR   --cli passenger --phone=5551234567");
            return;
        }
        List<PassengerItineraryEntry> entries = bookingService.getPassengerItinerary(name, phone);
        for (PassengerItineraryEntry e : entries) {
            System.out.println(e.customerName() + " (" + e.customerPhone() + ")  "
                    + e.flightNumber() + " leg " + e.legNo() + " on " + e.date()
                    + "  " + e.depAirportCode() + " " + e.scheduledDepTime()
                    + " -> " + e.arrAirportCode() + " " + e.scheduledArrTime()
                    + "  seat " + e.seatNumber());
        }
    }

    private void runUtilization(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Usage: --cli utilization <START_DATE> <END_DATE>");
            return;
        }
        List<AircraftUtilization> rows = aircraftUtilizationService.getUtilizationReport(
                LocalDate.parse(args.get(0)), LocalDate.parse(args.get(1)));
        for (AircraftUtilization r : rows) {
            System.out.println(r.airplaneId() + "  type=" + r.airplaneType() + "  flights=" + r.totalFlights());
        }
    }

    private static String singleOption(ApplicationArguments args, String name) {
        List<String> values = args.getOptionValues(name);
        return (values == null || values.isEmpty() || values.get(0).isBlank()) ? null : values.get(0);
    }

    private static void printUsage() {
        System.out.println("""
                Milestone 2 CLI commands:
                  --cli flight <FLIGHT_NUMBER>
                  --cli trip <ORIGIN> <DESTINATION>
                  --cli availability <FLIGHT_NUMBER> <YYYY-MM-DD>
                  --cli passenger --name="Jane Smith"
                  --cli passenger --phone=5551234567
                  --cli utilization <START_DATE> <END_DATE>
                """);
    }
}

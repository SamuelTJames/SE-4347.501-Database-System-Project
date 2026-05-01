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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.TerminalBuilder;

@Component
@Order(2)
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
        Thread repl = new Thread(this::loop, "cli-repl");
        repl.setDaemon(true);
        repl.start();
    }

    private void loop() {
        try {
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(TerminalBuilder.builder()
                            .system(true)
                            .type(System.getenv().getOrDefault("TERM", "xterm-256color"))
                            .build())
                    .completer(new StringsCompleter(
                            "flight(", "trip(", "availability(", "passenger(",
                            "utilization(", "help;", "exit;"))
                    .build();
            printBanner();
            while (true) {
                String line = "";
                try {
                    line = reader.readLine("prompt> ").trim();
                } catch (UserInterruptException e) {
                    System.out.println("Bye."); System.exit(0);
                } catch (EndOfFileException e) {
                    break;    // Ctrl+D / stdin closed
                }
                if (line.isEmpty()) continue;
                if (line.endsWith(";")) line = line.substring(0, line.length() - 1).trim();
                if (line.isEmpty()) continue;
                if (line.equals("exit") || line.equals("quit")) { System.out.println("Bye."); System.exit(0); }
                if (line.equals("help")) { printHelp(); continue; }
                if (line.contains(";")) { System.out.println("One command per line."); System.out.println(); continue; }

            int paren = line.indexOf('(');
            if (paren < 0) { System.out.println("Syntax error. Type help; for commands."); System.out.println(); continue; }

            String command = line.substring(0, paren).trim().toLowerCase();
            String argsStr = line.substring(paren + 1);
            if (argsStr.endsWith(")")) argsStr = argsStr.substring(0, argsStr.length() - 1);

            List<String> positional = new ArrayList<>();
            Map<String, String> named = new LinkedHashMap<>();
            parseArgs(argsStr, positional, named);

            try {
                switch (command) {
                    case "flight"       -> runFlight(positional);
                    case "trip"         -> runTrip(positional);
                    case "availability" -> runAvailability(positional);
                    case "passenger"    -> runPassenger(named);
                    case "utilization"  -> runUtilization(positional);
                    default -> System.out.println("Unknown command: " + command + ". Type help;");
                }
            } catch (NotFoundException | InvalidInputException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date — use YYYY-MM-DD format.");
            } catch (Exception ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
            System.out.println();
            }
        } catch (Exception e) {
            System.out.println("[cli] Terminal unavailable: " + e.getMessage());
        }
    }

    private void runFlight(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Usage: flight(\"AA3478\");");
            return;
        }
        FlightDetails d = flightQueryService.getFlightByNumber(args.get(0));
        System.out.println("Flight " + d.number() + " (" + d.airline() + ")  weekdays=" + d.weekdays());

        System.out.println("\nLegs:");
        List<String[]> legRows = new ArrayList<>();
        d.legs().forEach(l -> legRows.add(new String[]{
                String.valueOf(l.legNo()), l.depAirport().airportCode(), l.arrAirport().airportCode(),
                l.scheduledDepTime().toString(), l.scheduledArrTime().toString()}));
        printTable(new String[]{"LEG", "FROM", "TO", "DEP", "ARR"}, legRows);

        if (!d.fares().isEmpty()) {
            System.out.println("\nFares:");
            List<String[]> fareRows = new ArrayList<>();
            d.fares().forEach(f -> fareRows.add(new String[]{
                    f.code(), "$" + f.amount(), f.restriction() != null ? f.restriction() : ""}));
            printTable(new String[]{"CODE", "AMOUNT", "RESTRICTION"}, fareRows);
        }
    }

    private void runTrip(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Usage: trip(\"DFW\", \"SFO\");");
            return;
        }
        ItineraryResults results = itineraryService.findItineraries(args.get(0), args.get(1));

        System.out.println("Direct (" + results.direct().size() + "):");
        if (results.direct().isEmpty()) {
            System.out.println("  (none)");
        } else {
            List<String[]> rows = new ArrayList<>();
            for (DirectItinerary d : results.direct())
                rows.add(new String[]{d.flightNumber(), String.valueOf(d.legNo()),
                        d.origin().airportCode(), d.scheduledDepTime().toString(),
                        d.destination().airportCode(), d.scheduledArrTime().toString()});
            printTable(new String[]{"FLIGHT", "LEG", "FROM", "DEP", "TO", "ARR"}, rows);
        }

        System.out.println("\nOne-connection (" + results.oneStop().size() + "):");
        if (results.oneStop().isEmpty()) {
            System.out.println("  (none)");
        } else {
            List<String[]> rows = new ArrayList<>();
            for (OneStopItinerary o : results.oneStop())
                rows.add(new String[]{
                        o.firstLeg().flightNumber(), o.firstLeg().origin().airportCode(),
                        o.firstLeg().scheduledDepTime().toString(), o.connection().airportCode(),
                        o.firstLeg().scheduledArrTime().toString(),
                        o.secondLeg().flightNumber(), o.secondLeg().scheduledDepTime().toString(),
                        o.secondLeg().destination().airportCode(), o.secondLeg().scheduledArrTime().toString()});
            printTable(new String[]{"FLIGHT1", "FROM", "DEP", "VIA", "ARR", "FLIGHT2", "DEP", "TO", "ARR"}, rows);
        }
    }

    private void runAvailability(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Usage: availability(\"AA3478\", \"2026-05-01\");");
            return;
        }
        List<SeatAvailability> rows = bookingService.checkSeatAvailability(args.get(0), LocalDate.parse(args.get(1)));
        if (rows.isEmpty()) { System.out.println("No scheduled instances found."); return; }
        List<String[]> tableRows = new ArrayList<>();
        for (SeatAvailability r : rows)
            tableRows.add(new String[]{r.flightNumber(), String.valueOf(r.legNo()), r.date().toString(),
                    r.airplaneId(), String.valueOf(r.airplaneCapacity()),
                    String.valueOf(r.confirmedReservations()), String.valueOf(r.remainingSeats())});
        printTable(new String[]{"FLIGHT", "LEG", "DATE", "AIRPLANE", "CAPACITY", "CONFIRMED", "REMAINING"}, tableRows);
    }

    private void runPassenger(Map<String, String> named) {
        String name  = named.get("name");
        String phone = named.get("phone");
        if (name == null && phone == null) {
            System.out.println("Usage: passenger(name=\"Jane Smith\");  OR  passenger(phone=\"5551234567\");");
            return;
        }
        List<PassengerItineraryEntry> entries = bookingService.getPassengerItinerary(name, phone);
        if (entries.isEmpty()) { System.out.println("No itinerary found."); return; }
        List<String[]> rows = new ArrayList<>();
        for (PassengerItineraryEntry e : entries)
            rows.add(new String[]{e.customerName(), e.customerPhone(),
                    e.flightNumber(), String.valueOf(e.legNo()), e.date().toString(),
                    e.depAirportCode(), e.scheduledDepTime().toString(),
                    e.arrAirportCode(), e.scheduledArrTime().toString(), e.seatNumber()});
        printTable(new String[]{"NAME", "PHONE", "FLIGHT", "LEG", "DATE", "FROM", "DEP", "TO", "ARR", "SEAT"}, rows);
    }

    private void runUtilization(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Usage: utilization(\"2026-05-01\", \"2026-05-31\");");
            return;
        }
        List<AircraftUtilization> rows = aircraftUtilizationService.getUtilizationReport(
                LocalDate.parse(args.get(0)), LocalDate.parse(args.get(1)));
        if (rows.isEmpty()) { System.out.println("No utilization data."); return; }
        List<String[]> tableRows = new ArrayList<>();
        for (AircraftUtilization r : rows)
            tableRows.add(new String[]{r.airplaneId(), r.airplaneType(), String.valueOf(r.totalFlights())});
        printTable(new String[]{"AIRPLANE", "TYPE", "FLIGHTS"}, tableRows);
    }

    private static void printTable(String[] headers, List<String[]> rows) {
        int cols = headers.length;
        int[] w = new int[cols];
        for (int i = 0; i < cols; i++) w[i] = headers[i].length();
        for (String[] row : rows)
            for (int i = 0; i < Math.min(cols, row.length); i++)
                w[i] = Math.max(w[i], row[i] != null ? row[i].length() : 0);

        System.out.println("  " + buildRow(headers, w));
        StringBuilder sep = new StringBuilder("  ");
        for (int i = 0; i < cols; i++) { sep.append("-".repeat(w[i])); if (i < cols - 1) sep.append("  "); }
        System.out.println(sep);
        for (String[] row : rows) System.out.println("  " + buildRow(row, w));
    }

    private static String buildRow(String[] cells, int[] w) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < w.length; i++) {
            String c = (i < cells.length && cells[i] != null) ? cells[i] : "";
            sb.append(c).append(" ".repeat(Math.max(0, w[i] - c.length())));
            if (i < w.length - 1) sb.append("  ");
        }
        return sb.toString();
    }

    private static void parseArgs(String argsStr, List<String> positional, Map<String, String> named) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        for (char c : argsStr.toCharArray()) {
            if (c == '"') { inQuote = !inQuote; cur.append(c); }
            else if (c == ',' && !inQuote) { parts.add(cur.toString()); cur = new StringBuilder(); }
            else { cur.append(c); }
        }
        if (!cur.isEmpty()) parts.add(cur.toString());

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;
            int eq = part.indexOf('=');
            if (eq > 0 && part.charAt(0) != '"') {
                named.put(part.substring(0, eq).trim(), stripQuotes(part.substring(eq + 1).trim()));
            } else {
                positional.add(stripQuotes(part));
            }
        }
    }

    private static String stripQuotes(String s) {
        if (s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"')
            return s.substring(1, s.length() - 1);
        return s;
    }

    private static void printBanner() {
        System.out.println("""

                Airline DB — Interactive CLI
                Type  help;  for available commands.  Type  exit;  to quit.
                """);
    }

    private static void printHelp() {
        System.out.println("""
                Commands:
                  flight("AA3478");                            — legs and fares for a flight number
                  trip("DFW", "SFO");                         — direct and one-stop itineraries (code or city name)
                  availability("AA3478", "2026-05-01");        — seat capacity vs confirmed reservations for a flight on a date
                  passenger(name="Jane Smith");                — all booked legs for a passenger by name
                  passenger(phone="5551234567");               — all booked legs for a passenger by phone
                  utilization("2026-05-01", "2026-05-31");     — total flights per airplane for a date range
                  help;
                  exit;
                """);
    }
}

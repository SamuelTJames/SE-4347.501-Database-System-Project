package com.se4347.database_system_project.cli;

import com.se4347.database_system_project.dao.jpa.AirplaneRepository;
import com.se4347.database_system_project.dao.jpa.AirplaneTypeRepository;
import com.se4347.database_system_project.dao.jpa.AirportRepository;
import com.se4347.database_system_project.dao.jpa.FareRepository;
import com.se4347.database_system_project.dao.jpa.FlightLegRepository;
import com.se4347.database_system_project.dao.jpa.FlightRepository;
import com.se4347.database_system_project.dao.jpa.LegInstanceRepository;
import com.se4347.database_system_project.dao.jpa.SeatRepository;
import com.se4347.database_system_project.domain.Airplane;
import com.se4347.database_system_project.domain.AirplaneType;
import com.se4347.database_system_project.domain.Airport;
import com.se4347.database_system_project.domain.Fare;
import com.se4347.database_system_project.domain.Flight;
import com.se4347.database_system_project.domain.FlightLeg;
import com.se4347.database_system_project.domain.LegInstance;
import com.se4347.database_system_project.domain.Seat;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Order(1)
public class DevDataSeeder implements ApplicationRunner {

    private final AirportRepository airports;
    private final AirplaneTypeRepository airplaneTypes;
    private final AirplaneRepository airplanes;
    private final FlightRepository flights;
    private final FlightLegRepository flightLegs;
    private final FareRepository fares;
    private final LegInstanceRepository legInstances;
    private final SeatRepository seats;

    public DevDataSeeder(AirportRepository airports,
                         AirplaneTypeRepository airplaneTypes,
                         AirplaneRepository airplanes,
                         FlightRepository flights,
                         FlightLegRepository flightLegs,
                         FareRepository fares,
                         LegInstanceRepository legInstances,
                         SeatRepository seats) {
        this.airports = airports;
        this.airplaneTypes = airplaneTypes;
        this.airplanes = airplanes;
        this.flights = flights;
        this.flightLegs = flightLegs;
        this.fares = fares;
        this.legInstances = legInstances;
        this.seats = seats;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (airports.count() > 0) {
            System.out.println("[seeder] Database already populated — skipping seed.");
            return;
        }

        Airport dfw = airport("DFW", "Dallas/Fort Worth Intl", "Dallas", "TX");
        Airport sfo = airport("SFO", "San Francisco Intl", "San Francisco", "CA");
        Airport atl = airport("ATL", "Hartsfield-Jackson", "Atlanta", "GA");
        Airport jfk = airport("JFK", "John F. Kennedy Intl", "New York", "NY");

        AirplaneType b737 = new AirplaneType();
        b737.setTypeName("B737");
        b737.setCompany("Boeing");
        b737.setMaxSeats(180);
        airplaneTypes.save(b737);

        Airplane n101 = airplane("N101AA", 150, b737);
        Airplane n102 = airplane("N102AA", 160, b737);
        airplane("N103AA", 170, b737); // idle — appears in utilization report with 0 flights

        Flight aa3478 = flight("AA3478", "AA", "MTWHFSU");
        Flight aa1000 = flight("AA1000", "AA", "MTWHFSU");
        Flight aa2000 = flight("AA2000", "AA", "MTWHFSU");
        Flight ua9999 = flight("UA9999", "UA", "MTWHF__");

        leg(aa3478, 1, dfw, sfo, LocalTime.of(8, 0),  LocalTime.of(10, 30));
        leg(aa1000, 2, dfw, atl, LocalTime.of(9, 0),  LocalTime.of(12, 0));
        leg(aa2000, 3, atl, sfo, LocalTime.of(13, 0), LocalTime.of(16, 0));
        leg(ua9999, 4, sfo, jfk, LocalTime.of(7, 0),  LocalTime.of(15, 0));

        fare(aa3478, "Y", new BigDecimal("250.00"), null);
        fare(aa3478, "F", new BigDecimal("550.00"), "First class");

        LocalDate date = LocalDate.of(2026, 5, 1);
        legInstance(date, aa3478.getNumber(), 1, n101);
        legInstance(date, aa1000.getNumber(), 2, n102);

        seat("12A", date, aa3478.getNumber(), 1, "Jane Smith", "5551234567");

        System.out.println("[seeder] Sample data loaded — 4 airports, 4 flights, 2 leg instances, 1 seat.");
    }

    private Airport airport(String code, String name, String city, String state) {
        Airport a = new Airport();
        a.setAirportCode(code);
        a.setName(name);
        a.setCity(city);
        a.setState(state);
        return airports.save(a);
    }

    private Airplane airplane(String id, int totalSeats, AirplaneType type) {
        Airplane a = new Airplane();
        a.setAirplaneId(id);
        a.setTotalNoOfSeats(totalSeats);
        a.setAirplaneType(type);
        return airplanes.save(a);
    }

    private Flight flight(String number, String airline, String weekdays) {
        Flight f = new Flight();
        f.setNumber(number);
        f.setAirline(airline);
        f.setWeekdays(weekdays);
        return flights.save(f);
    }

    private void leg(Flight flight, int legNo, Airport dep, Airport arr,
                     LocalTime depTime, LocalTime arrTime) {
        FlightLeg l = new FlightLeg();
        l.setId(new FlightLeg.FlightLegId(flight.getNumber(), legNo));
        l.setFlight(flight);
        l.setDepAirport(dep);
        l.setArrAirport(arr);
        l.setScheduledDepTime(depTime);
        l.setScheduledArrTime(arrTime);
        flightLegs.save(l);
    }

    private void fare(Flight flight, String code, BigDecimal amount, String restriction) {
        Fare f = new Fare();
        f.setId(new Fare.FareId(code, flight.getNumber()));
        f.setFlight(flight);
        f.setAmount(amount);
        f.setRestriction(restriction);
        fares.save(f);
    }

    private void legInstance(LocalDate date, String flightNumber, int legNo, Airplane airplane) {
        LegInstance li = new LegInstance();
        li.setId(new LegInstance.LegInstanceId(date, flightNumber, legNo));
        li.setFlightLeg(flightLegs.getReferenceById(new FlightLeg.FlightLegId(flightNumber, legNo)));
        li.setNoOfAvailSeats(airplane.getTotalNoOfSeats());
        li.setAirplane(airplane);
        legInstances.save(li);
    }

    private void seat(String seatNo, LocalDate date, String flightNumber, int legNo,
                      String name, String phone) {
        Seat s = new Seat();
        s.setId(new Seat.SeatId(seatNo, date, flightNumber, legNo));
        s.setCustomerName(name);
        s.setCustomerPhone(phone);
        seats.save(s);
    }
}

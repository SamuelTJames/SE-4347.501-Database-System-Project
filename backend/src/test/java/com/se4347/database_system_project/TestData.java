package com.se4347.database_system_project;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Seeds a small but complete world used by every service test:
 *   - 4 airports: DFW, JFK, SFO, ATL
 *   - 1 airplane type B737, 2 airplanes
 *   - Flight AA3478: DFW -> SFO direct (legNo 1)
 *   - Flight AA1000: DFW -> ATL (legNo 2), AA2000: ATL -> SFO (legNo 3)  -> a one-stop DFW->SFO
 *   - Flight UA9999: SFO -> JFK (legNo 4) for "no match" tests
 *   - LegInstances on 2026-05-01 for AA3478/leg1 and AA1000/leg2; one confirmed seat on AA3478/leg1.
 */
@Component
public class TestData {

    public static final LocalDate FLIGHT_DATE = LocalDate.of(2026, 5, 1);

    private final AirportRepository airports;
    private final AirplaneTypeRepository airplaneTypes;
    private final AirplaneRepository airplanes;
    private final FlightRepository flights;
    private final FlightLegRepository flightLegs;
    private final FareRepository fares;
    private final LegInstanceRepository legInstances;
    private final SeatRepository seats;

    public TestData(AirportRepository airports,
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

    @Transactional
    public void clearAll() {
        seats.deleteAll();
        legInstances.deleteAll();
        fares.deleteAll();
        flightLegs.deleteAll();
        flights.deleteAll();
        airplanes.deleteAll();
        airplaneTypes.deleteAll();
        airports.deleteAll();
    }

    @Transactional
    public void seed() {
        Airport dfw = saveAirport("DFW", "Dallas/Fort Worth Intl", "Dallas", "TX");
        Airport sfo = saveAirport("SFO", "San Francisco Intl", "San Francisco", "CA");
        Airport atl = saveAirport("ATL", "Hartsfield-Jackson", "Atlanta", "GA");
        Airport jfk = saveAirport("JFK", "John F. Kennedy Intl", "New York", "NY");

        AirplaneType b737 = new AirplaneType();
        b737.setTypeName("B737");
        b737.setCompany("Boeing");
        b737.setMaxSeats(180);
        airplaneTypes.save(b737);

        Airplane n101 = saveAirplane("N101AA", 150, b737);
        Airplane n102 = saveAirplane("N102AA", 160, b737);
        // N103 has no flights — proves the report includes idle airplanes.
        saveAirplane("N103AA", 170, b737);

        Flight aa3478 = saveFlight("AA3478", "AA", "MTWHFSU");
        Flight aa1000 = saveFlight("AA1000", "AA", "MTWHFSU");
        Flight aa2000 = saveFlight("AA2000", "AA", "MTWHFSU");
        Flight ua9999 = saveFlight("UA9999", "UA", "MTWHF__");

        // Direct DFW -> SFO at legNo 1
        saveLeg(aa3478, 1, dfw, sfo, LocalTime.of(8, 0), LocalTime.of(10, 30));
        // One-stop DFW -> ATL -> SFO via legs 2 and 3
        saveLeg(aa1000, 2, dfw, atl, LocalTime.of(9, 0), LocalTime.of(12, 0));
        saveLeg(aa2000, 3, atl, sfo, LocalTime.of(13, 0), LocalTime.of(16, 0));
        // Unrelated leg
        saveLeg(ua9999, 4, sfo, jfk, LocalTime.of(7, 0), LocalTime.of(15, 0));

        saveFare(aa3478, "Y", new BigDecimal("250.00"), null);
        saveFare(aa3478, "F", new BigDecimal("550.00"), "First class");

        saveLegInstance(FLIGHT_DATE, aa3478.getNumber(), 1, n101);
        saveLegInstance(FLIGHT_DATE, aa1000.getNumber(), 2, n102);
        // No instance for leg 3 on this date — exercises "no scheduled instance" paths.

        saveSeat("12A", FLIGHT_DATE, aa3478.getNumber(), 1, "Jane Smith", "5551234567");
    }

    private Airport saveAirport(String code, String name, String city, String state) {
        Airport a = new Airport();
        a.setAirportCode(code);
        a.setName(name);
        a.setCity(city);
        a.setState(state);
        return airports.save(a);
    }

    private Airplane saveAirplane(String id, int seats, AirplaneType type) {
        Airplane a = new Airplane();
        a.setAirplaneId(id);
        a.setTotalNoOfSeats(seats);
        a.setAirplaneType(type);
        return airplanes.save(a);
    }

    private Flight saveFlight(String number, String airline, String weekdays) {
        Flight f = new Flight();
        f.setNumber(number);
        f.setAirline(airline);
        f.setWeekdays(weekdays);
        return flights.save(f);
    }

    private void saveLeg(Flight flight, int legNo, Airport dep, Airport arr, LocalTime depTime, LocalTime arrTime) {
        FlightLeg leg = new FlightLeg();
        leg.setId(new FlightLeg.FlightLegId(flight.getNumber(), legNo));
        leg.setFlight(flight);
        leg.setDepAirport(dep);
        leg.setArrAirport(arr);
        leg.setScheduledDepTime(depTime);
        leg.setScheduledArrTime(arrTime);
        flightLegs.save(leg);
    }

    private void saveFare(Flight flight, String code, BigDecimal amount, String restriction) {
        Fare f = new Fare();
        f.setId(new Fare.FareId(code, flight.getNumber()));
        f.setFlight(flight);
        f.setAmount(amount);
        f.setRestriction(restriction);
        fares.save(f);
    }

    private void saveLegInstance(LocalDate date, String flightNumber, int legNo, Airplane airplane) {
        LegInstance li = new LegInstance();
        li.setId(new LegInstance.LegInstanceId(date, flightNumber, legNo));
        li.setFlightLeg(flightLegs.getReferenceById(new FlightLeg.FlightLegId(flightNumber, legNo)));
        li.setNoOfAvailSeats(airplane.getTotalNoOfSeats());
        li.setAirplane(airplane);
        legInstances.save(li);
    }

    private void saveSeat(String seatNo, LocalDate date, String flightNumber, int legNo, String name, String phone) {
        Seat s = new Seat();
        s.setId(new Seat.SeatId(seatNo, date, flightNumber, legNo));
        s.setCustomerName(name);
        s.setCustomerPhone(phone);
        seats.save(s);
    }
}

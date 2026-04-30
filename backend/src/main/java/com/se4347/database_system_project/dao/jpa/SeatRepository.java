package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Seat.SeatId> {

    @Query("""
            SELECT s.id.legNo, COUNT(s)
            FROM Seat s
            WHERE s.id.date = :date
              AND s.id.flightNumber = :flightNumber
            GROUP BY s.id.legNo
            """)
    List<Object[]> findConfirmedSeatCountsForFlightOnDate(@Param("date") LocalDate date,
                                                           @Param("flightNumber") String flightNumber);

    @Query("""
            SELECT new com.se4347.database_system_project.api.dto.PassengerItineraryEntry(
                s.customerName, s.customerPhone,
                l.id.flightNumber, l.id.legNo, s.id.date,
                l.depAirport.airportCode, l.arrAirport.airportCode,
                l.scheduledDepTime, l.scheduledArrTime,
                s.id.seatNo
            )
            FROM Seat s
            JOIN s.legInstance li
            JOIN li.flightLeg l
            WHERE LOWER(s.customerName) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY s.id.date ASC, l.id.flightNumber ASC, l.id.legNo ASC
            """)
    List<PassengerItineraryEntry> findItineraryByCustomerName(@Param("name") String name);

    @Query("""
            SELECT new com.se4347.database_system_project.api.dto.PassengerItineraryEntry(
                s.customerName, s.customerPhone,
                l.id.flightNumber, l.id.legNo, s.id.date,
                l.depAirport.airportCode, l.arrAirport.airportCode,
                l.scheduledDepTime, l.scheduledArrTime,
                s.id.seatNo
            )
            FROM Seat s
            JOIN s.legInstance li
            JOIN li.flightLeg l
            WHERE s.customerPhone = :phone
            ORDER BY s.id.date ASC, l.id.flightNumber ASC, l.id.legNo ASC
            """)
    List<PassengerItineraryEntry> findItineraryByCustomerPhone(@Param("phone") String phone);
}

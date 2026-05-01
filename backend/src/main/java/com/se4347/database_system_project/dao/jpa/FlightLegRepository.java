package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.FlightLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface FlightLegRepository extends JpaRepository<FlightLeg, FlightLeg.FlightLegId> {

    @Query("""
            SELECT l FROM FlightLeg l
            JOIN FETCH l.flight
            JOIN FETCH l.depAirport
            JOIN FETCH l.arrAirport
            WHERE l.id.flightNumber = :flightNumber
            ORDER BY l.id.legNo ASC
            """)
    List<FlightLeg> findByFlightNumber(@Param("flightNumber") String flightNumber);

    @Query("""
            SELECT l FROM FlightLeg l
            JOIN FETCH l.flight
            JOIN FETCH l.depAirport
            JOIN FETCH l.arrAirport
            WHERE l.depAirport.airportCode IN :originCodes
              AND l.arrAirport.airportCode IN :destinationCodes
            ORDER BY l.scheduledDepTime ASC, l.id.flightNumber ASC, l.id.legNo ASC
            """)
    List<FlightLeg> findDirectLegs(@Param("originCodes") Collection<String> originCodes,
                                   @Param("destinationCodes") Collection<String> destinationCodes);

    /**
     * One-stop itinerary: leg pairs (l1, l2) where l1 starts at origin, l2 ends at destination,
     * the connection airport is neither origin nor destination, and l2 departs after l1 arrives.
     * Returns each row as Object[] = { FlightLeg first, FlightLeg second } so the service can
     * assemble both halves of the trip.
     */
    @Query("""
            SELECT l1, l2 FROM FlightLeg l1, FlightLeg l2
            WHERE l1.depAirport.airportCode IN :originCodes
              AND l2.arrAirport.airportCode IN :destinationCodes
              AND l1.arrAirport.airportCode = l2.depAirport.airportCode
              AND l1.arrAirport.airportCode NOT IN :originCodes
              AND l1.arrAirport.airportCode NOT IN :destinationCodes
              AND (l1.id.flightNumber <> l2.id.flightNumber OR l1.id.legNo <> l2.id.legNo)
              AND l2.scheduledDepTime > l1.scheduledArrTime
            ORDER BY l1.scheduledDepTime ASC, l2.scheduledArrTime ASC
            """)
    List<Object[]> findOneStopLegPairs(@Param("originCodes") Collection<String> originCodes,
                                       @Param("destinationCodes") Collection<String> destinationCodes);
}

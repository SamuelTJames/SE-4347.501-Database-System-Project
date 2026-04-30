package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "FLIGHT_LEG")
public class FlightLeg {

    // This table has no single unique column, so the PK is a combination of two columns.
    // JPA requires a separate class to represent that composite key.
    @Embeddable
    public static class FlightLegId implements Serializable {

        @Column(name = "NUMBER", nullable = false)
        private String flightNumber;

        @Column(name = "LEG_NO", nullable = false)
        private int legNo;

        public FlightLegId() {}

        public FlightLegId(String flightNumber, int legNo) {
            this.flightNumber = flightNumber;
            this.legNo = legNo;
        }

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

        public int getLegNo() { return legNo; }
        public void setLegNo(int legNo) { this.legNo = legNo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FlightLegId that)) return false;
            return legNo == that.legNo && Objects.equals(flightNumber, that.flightNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(flightNumber, legNo);
        }
    }

    // Tells JPA to use the composite key class above as this entity's primary key.
    @EmbeddedId
    private FlightLegId id;

    // NUMBER is both an FK to FLIGHT and part of the PK.
    // @MapsId tells JPA to automatically fill id.flightNumber from this relationship,
    // so you only need to set the Flight object — no need to set the PK field manually.
    @ManyToOne
    @MapsId("flightNumber")
    @JoinColumn(name = "NUMBER", nullable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "DEP_AIRPORT_CODE", nullable = false)
    private Airport depAirport;

    @ManyToOne
    @JoinColumn(name = "ARR_AIRPORT_CODE", nullable = false)
    private Airport arrAirport;

    @Column(name = "SCHEDULED_DEP_TIME", nullable = false)
    private LocalTime scheduledDepTime;

    @Column(name = "SCHEDULED_ARR_TIME", nullable = false)
    private LocalTime scheduledArrTime;

    public FlightLeg() {}

    public FlightLegId getId() { return id; }
    public void setId(FlightLegId id) { this.id = id; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Airport getDepAirport() { return depAirport; }
    public void setDepAirport(Airport depAirport) { this.depAirport = depAirport; }

    public Airport getArrAirport() { return arrAirport; }
    public void setArrAirport(Airport arrAirport) { this.arrAirport = arrAirport; }

    public LocalTime getScheduledDepTime() { return scheduledDepTime; }
    public void setScheduledDepTime(LocalTime scheduledDepTime) { this.scheduledDepTime = scheduledDepTime; }

    public LocalTime getScheduledArrTime() { return scheduledArrTime; }
    public void setScheduledArrTime(LocalTime scheduledArrTime) { this.scheduledArrTime = scheduledArrTime; }
}

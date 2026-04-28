package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "FLIGHT_LEG")
public class FlightLeg {

    @EmbeddedId
    private FlightLegId id;

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

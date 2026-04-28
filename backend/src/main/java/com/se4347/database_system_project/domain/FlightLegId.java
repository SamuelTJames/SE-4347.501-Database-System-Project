package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FlightLegId implements Serializable {

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

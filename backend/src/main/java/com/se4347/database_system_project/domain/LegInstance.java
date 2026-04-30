package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "LEG_INSTANCE")
public class LegInstance {

    @Embeddable
    public static class LegInstanceId implements Serializable {

        @Column(name = "DATE", nullable = false)
        private LocalDate date;

        @Column(name = "NUMBER", nullable = false)
        private String flightNumber;

        @Column(name = "LEG_NO", nullable = false)
        private int legNo;

        public LegInstanceId() {}

        public LegInstanceId(LocalDate date, String flightNumber, int legNo) {
            this.date = date;
            this.flightNumber = flightNumber;
            this.legNo = legNo;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

        public int getLegNo() { return legNo; }
        public void setLegNo(int legNo) { this.legNo = legNo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LegInstanceId that)) return false;
            return legNo == that.legNo
                    && Objects.equals(date, that.date)
                    && Objects.equals(flightNumber, that.flightNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, flightNumber, legNo);
        }
    }

    @EmbeddedId
    private LegInstanceId id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "NUMBER", referencedColumnName = "NUMBER", insertable = false, updatable = false),
            @JoinColumn(name = "LEG_NO", referencedColumnName = "LEG_NO", insertable = false, updatable = false)
    })
    private FlightLeg flightLeg;

    @Column(name = "NO_OF_AVAIL_SEATS", nullable = false)
    private int noOfAvailSeats;

    @ManyToOne
    @JoinColumn(name = "AIRPLANE_ID", nullable = false)
    private Airplane airplane;

    public LegInstance() {}

    public LegInstanceId getId() { return id; }
    public void setId(LegInstanceId id) { this.id = id; }

    public FlightLeg getFlightLeg() { return flightLeg; }
    public void setFlightLeg(FlightLeg flightLeg) { this.flightLeg = flightLeg; }

    public int getNoOfAvailSeats() { return noOfAvailSeats; }
    public void setNoOfAvailSeats(int noOfAvailSeats) { this.noOfAvailSeats = noOfAvailSeats; }

    public Airplane getAirplane() { return airplane; }
    public void setAirplane(Airplane airplane) { this.airplane = airplane; }
}

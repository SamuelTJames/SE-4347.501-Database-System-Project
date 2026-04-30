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
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "FARE")
public class Fare {

    // Composite PK: a fare is uniquely identified by its code AND which flight it belongs to.
    @Embeddable
    public static class FareId implements Serializable {

        @Column(name = "CODE", length = 10, nullable = false)
        private String code;

        @Column(name = "NUMBER", length = 10, nullable = false)
        private String flightNumber;

        public FareId() {}

        public FareId(String code, String flightNumber) {
            this.code = code;
            this.flightNumber = flightNumber;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FareId that)) return false;
            return Objects.equals(code, that.code) && Objects.equals(flightNumber, that.flightNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, flightNumber);
        }
    }

    @EmbeddedId
    private FareId id;

    // NUMBER is both an FK to FLIGHT and part of the PK.
    // @MapsId tells JPA to automatically fill id.flightNumber from this relationship,
    // so you only need to set the Flight object — no need to set the PK field manually.
    @ManyToOne
    @MapsId("flightNumber")
    @JoinColumn(name = "NUMBER", nullable = false)
    private Flight flight;

    @Column(name = "AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "RESTRICTION")
    private String restriction;

    public Fare() {}

    public FareId getId() { return id; }
    public void setId(FareId id) { this.id = id; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getRestriction() { return restriction; }
    public void setRestriction(String restriction) { this.restriction = restriction; }
}

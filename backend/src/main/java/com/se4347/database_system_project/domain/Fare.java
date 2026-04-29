package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "FARE")
public class Fare {

    @Id
    @Column(name = "CODE", length = 10, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "NUMBER", nullable = false)
    private Flight flight;

    @Column(name = "AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "RESTRICTION")
    private String restriction;

    public Fare() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getRestriction() { return restriction; }
    public void setRestriction(String restriction) { this.restriction = restriction; }
}

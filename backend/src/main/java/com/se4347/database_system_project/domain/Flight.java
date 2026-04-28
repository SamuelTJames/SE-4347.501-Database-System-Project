package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FLIGHT")
public class Flight {

    @Id
    @Column(name = "NUMBER", length = 10, nullable = false)
    private String number;

    @Column(name = "AIRLINE", length = 2, nullable = false)
    private String airline;

    @Column(name = "WEEKDAYS", length = 7, nullable = false)
    private String weekdays;

    public Flight() {}

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public String getWeekdays() { return weekdays; }
    public void setWeekdays(String weekdays) { this.weekdays = weekdays; }
}

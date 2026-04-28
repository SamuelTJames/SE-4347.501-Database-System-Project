package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "AIRPORT")
public class Airport {

    @Id
    @Column(name = "AIRPORT_CODE", length = 3, nullable = false)
    private String airportCode;

    @Column(name = "NAME", length = 52, nullable = false)
    private String name;

    @Column(name = "CITY", length = 23, nullable = false)
    private String city;

    @Column(name = "STATE", length = 4, nullable = false)
    private String state;

    public Airport() {}

    public String getAirportCode() { return airportCode; }
    public void setAirportCode(String airportCode) { this.airportCode = airportCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}

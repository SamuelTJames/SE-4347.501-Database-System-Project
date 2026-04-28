package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "AIRPLANE")
public class Airplane {

    @Id
    @Column(name = "AIRPLANE_ID", length = 11, nullable = false)
    private String airplaneId;

    @Column(name = "TOTAL_NO_OF_SEATS", nullable = false)
    private int totalNoOfSeats;

    @ManyToOne
    @JoinColumn(name = "TYPE_NAME", nullable = false)
    private AirplaneType airplaneType;

    public Airplane() {}

    public String getAirplaneId() { return airplaneId; }
    public void setAirplaneId(String airplaneId) { this.airplaneId = airplaneId; }

    public int getTotalNoOfSeats() { return totalNoOfSeats; }
    public void setTotalNoOfSeats(int totalNoOfSeats) { this.totalNoOfSeats = totalNoOfSeats; }

    public AirplaneType getAirplaneType() { return airplaneType; }
    public void setAirplaneType(AirplaneType airplaneType) { this.airplaneType = airplaneType; }
}

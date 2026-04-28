package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "LEG_INSTANCE")
public class LegInstance {

    @EmbeddedId
    private LegInstanceId id;

    @Column(name = "NO_OF_AVAIL_SEATS", nullable = false)
    private int noOfAvailSeats;

    @ManyToOne
    @JoinColumn(name = "AIRPLANE_ID", nullable = false)
    private Airplane airplane;

    public LegInstance() {}

    public LegInstanceId getId() { return id; }
    public void setId(LegInstanceId id) { this.id = id; }

    public int getNoOfAvailSeats() { return noOfAvailSeats; }
    public void setNoOfAvailSeats(int noOfAvailSeats) { this.noOfAvailSeats = noOfAvailSeats; }

    public Airplane getAirplane() { return airplane; }
    public void setAirplane(Airplane airplane) { this.airplane = airplane; }
}

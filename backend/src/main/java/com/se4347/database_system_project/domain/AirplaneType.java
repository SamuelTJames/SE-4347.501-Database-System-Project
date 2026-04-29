package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "AIRPLANE_TYPE")
public class AirplaneType {

    @Id
    @Column(name = "TYPE_NAME", length = 4, nullable = false)
    private String typeName;

    @Column(name = "COMPANY", length = 30, nullable = false)
    private String company;

    @Column(name = "MAX_SEATS", nullable = false)
    private int maxSeats;

    public AirplaneType() {}

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public int getMaxSeats() { return maxSeats; }
    public void setMaxSeats(int maxSeats) { this.maxSeats = maxSeats; }
}

package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "SEAT")
public class Seat {

    @EmbeddedId
    private SeatId id;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DATE", referencedColumnName = "DATE", insertable = false, updatable = false),
        @JoinColumn(name = "LEG_NO", referencedColumnName = "LEG_NO", insertable = false, updatable = false)
    })
    private LegInstance legInstance;

    @Column(name = "CUSTOMER_NAME", length = 30, nullable = false)
    private String customerName;

    @Column(name = "CPHONE", length = 15, nullable = false)
    private String customerPhone;

    public Seat() {}

    public SeatId getId() { return id; }
    public void setId(SeatId id) { this.id = id; }

    public LegInstance getLegInstance() { return legInstance; }
    public void setLegInstance(LegInstance legInstance) { this.legInstance = legInstance; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
}

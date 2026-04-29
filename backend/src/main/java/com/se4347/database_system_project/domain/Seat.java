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
@Table(name = "SEAT")
public class Seat {

    @Embeddable
    public static class SeatId implements Serializable {

        @Column(name = "SEAT_NO", length = 4, nullable = false)
        private String seatNo;

        @Column(name = "DATE", nullable = false)
        private LocalDate date;

        @Column(name = "LEG_NO", nullable = false)
        private int legNo;

        public SeatId() {}

        public SeatId(String seatNo, LocalDate date, int legNo) {
            this.seatNo = seatNo;
            this.date = date;
            this.legNo = legNo;
        }

        public String getSeatNo() { return seatNo; }
        public void setSeatNo(String seatNo) { this.seatNo = seatNo; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public int getLegNo() { return legNo; }
        public void setLegNo(int legNo) { this.legNo = legNo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SeatId that)) return false;
            return legNo == that.legNo && Objects.equals(seatNo, that.seatNo) && Objects.equals(date, that.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(seatNo, date, legNo);
        }
    }

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

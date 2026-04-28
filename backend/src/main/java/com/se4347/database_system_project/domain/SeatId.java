package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class SeatId implements Serializable {

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

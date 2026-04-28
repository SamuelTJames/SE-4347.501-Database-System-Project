package com.se4347.database_system_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class LegInstanceId implements Serializable {

    @Column(name = "DATE", nullable = false)
    private LocalDate date;

    @Column(name = "LEG_NO", nullable = false)
    private int legNo;

    public LegInstanceId() {}

    public LegInstanceId(LocalDate date, int legNo) {
        this.date = date;
        this.legNo = legNo;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getLegNo() { return legNo; }
    public void setLegNo(int legNo) { this.legNo = legNo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LegInstanceId that)) return false;
        return legNo == that.legNo && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, legNo);
    }
}

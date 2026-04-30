package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Seat.SeatId> {

    // TODO: BookingService
}

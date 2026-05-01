package com.se4347.database_system_project.api.dto;

import java.time.LocalDate;

public record SeatAvailability(String flightNumber,
                               int legNo,
                               LocalDate date,
                               String airplaneId,
                               int airplaneCapacity,
                               long confirmedReservations,
                               long remainingSeats) {}

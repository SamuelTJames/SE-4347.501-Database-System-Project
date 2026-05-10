package com.se4347.database_system_project.api.dto;

import java.time.LocalDate;

public record BookingConfirmation(
        String flightNumber,
        int legNo,
        LocalDate date,
        String seatNo,
        String customerName,
        String customerPhone) {}

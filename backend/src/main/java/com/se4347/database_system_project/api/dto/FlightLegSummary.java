package com.se4347.database_system_project.api.dto;

import java.time.LocalTime;

public record FlightLegSummary(int legNo,
                               AirportSummary depAirport,
                               AirportSummary arrAirport,
                               LocalTime scheduledDepTime,
                               LocalTime scheduledArrTime) {}

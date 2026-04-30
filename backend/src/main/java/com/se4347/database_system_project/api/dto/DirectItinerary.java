package com.se4347.database_system_project.api.dto;

import java.time.LocalTime;

public record DirectItinerary(String flightNumber,
                              String airline,
                              int legNo,
                              AirportSummary origin,
                              AirportSummary destination,
                              LocalTime scheduledDepTime,
                              LocalTime scheduledArrTime) {}

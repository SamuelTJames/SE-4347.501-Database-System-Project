package com.se4347.database_system_project.api.dto;

public record OneStopItinerary(DirectItinerary firstLeg,
                               DirectItinerary secondLeg,
                               AirportSummary connection) {}

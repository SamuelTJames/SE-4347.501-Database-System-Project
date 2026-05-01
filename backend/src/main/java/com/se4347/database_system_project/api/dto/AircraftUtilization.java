package com.se4347.database_system_project.api.dto;

public record AircraftUtilization(String airplaneId,
                                  String airplaneType,
                                  long totalFlights) {}

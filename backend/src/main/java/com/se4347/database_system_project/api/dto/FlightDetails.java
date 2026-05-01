package com.se4347.database_system_project.api.dto;

import java.util.List;

public record FlightDetails(String number,
                            String airline,
                            String weekdays,
                            List<FlightLegSummary> legs,
                            List<FareSummary> fares) {}

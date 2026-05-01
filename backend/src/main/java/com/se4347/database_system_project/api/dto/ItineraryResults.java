package com.se4347.database_system_project.api.dto;

import java.util.List;

public record ItineraryResults(List<DirectItinerary> direct,
                               List<OneStopItinerary> oneStop) {}

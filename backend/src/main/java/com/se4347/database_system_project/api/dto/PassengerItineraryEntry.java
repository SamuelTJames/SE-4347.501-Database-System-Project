package com.se4347.database_system_project.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record PassengerItineraryEntry(String customerName,
                                      String customerPhone,
                                      String flightNumber,
                                      int legNo,
                                      LocalDate date,
                                      String depAirportCode,
                                      String arrAirportCode,
                                      LocalTime scheduledDepTime,
                                      LocalTime scheduledArrTime,
                                      String seatNumber) {}

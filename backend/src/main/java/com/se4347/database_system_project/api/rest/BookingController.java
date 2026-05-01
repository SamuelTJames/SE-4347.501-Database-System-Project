package com.se4347.database_system_project.api.rest;

import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/availability")
    public List<SeatAvailability> availability(
            @RequestParam("flight") String flightNumber,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookingService.checkSeatAvailability(flightNumber, date);
    }

    @GetMapping("/passenger")
    public List<PassengerItineraryEntry> passengerItinerary(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone) {
        return bookingService.getPassengerItinerary(name, phone);
    }
}

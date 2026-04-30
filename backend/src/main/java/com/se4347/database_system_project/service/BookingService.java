package com.se4347.database_system_project.service;

import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.dao.jpa.LegInstanceRepository;
import com.se4347.database_system_project.dao.jpa.SeatRepository;
import com.se4347.database_system_project.domain.LegInstance;
import com.se4347.database_system_project.exception.InvalidInputException;
import com.se4347.database_system_project.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private final LegInstanceRepository legInstanceRepository;
    private final SeatRepository seatRepository;

    public BookingService(LegInstanceRepository legInstanceRepository,
                          SeatRepository seatRepository) {
        this.legInstanceRepository = legInstanceRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Returns seat availability for every leg of the given flight on the given date.
     * Capacity is taken from the airplane assigned to each leg instance; confirmed
     * reservations are counted from SEAT rows.
     */
    public List<SeatAvailability> checkSeatAvailability(String flightNumber, LocalDate date) {
        if (flightNumber == null || flightNumber.isBlank()) {
            throw new InvalidInputException("Flight number is required.");
        }
        if (date == null) {
            throw new InvalidInputException("Date is required.");
        }
        String normalized = flightNumber.trim().toUpperCase();

        List<LegInstance> instances = legInstanceRepository.findInstancesForFlight(normalized, date);
        if (instances.isEmpty()) {
            throw new NotFoundException(
                    "No scheduled instances for flight " + normalized + " on " + date + ".");
        }

        Map<Integer, Long> confirmedByLeg = new HashMap<>();
        for (Object[] row : seatRepository.findConfirmedSeatCountsForFlightOnDate(date, normalized)) {
            Integer legNo = (Integer) row[0];
            Long count = (Long) row[1];
            confirmedByLeg.put(legNo, count);
        }

        List<SeatAvailability> result = new ArrayList<>(instances.size());
        for (LegInstance li : instances) {
            int capacity = li.getAirplane().getTotalNoOfSeats();
            long confirmed = confirmedByLeg.getOrDefault(li.getId().getLegNo(), 0L);
            result.add(new SeatAvailability(
                    normalized,
                    li.getId().getLegNo(),
                    li.getId().getDate(),
                    li.getAirplane().getAirplaneId(),
                    capacity,
                    confirmed,
                    Math.max(0L, capacity - confirmed)));
        }
        return result;
    }

    /**
     * Look up every booked leg for a passenger by name (case-insensitive substring match)
     * or phone number (exact match). Exactly one of {name, phone} must be supplied.
     */
    public List<PassengerItineraryEntry> getPassengerItinerary(String name, String phone) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasPhone = phone != null && !phone.isBlank();
        if (hasName == hasPhone) {
            throw new InvalidInputException("Provide exactly one of customer name or phone.");
        }

        List<PassengerItineraryEntry> entries = hasName
                ? seatRepository.findItineraryByCustomerName(name.trim())
                : seatRepository.findItineraryByCustomerPhone(phone.trim());

        if (entries.isEmpty()) {
            throw new NotFoundException("No bookings found for the given passenger.");
        }
        return entries;
    }
}

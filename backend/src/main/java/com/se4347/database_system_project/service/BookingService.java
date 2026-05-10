package com.se4347.database_system_project.service;

import com.se4347.database_system_project.api.dto.BookingConfirmation;
import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.dao.jpa.LegInstanceRepository;
import com.se4347.database_system_project.dao.jpa.SeatRepository;
import com.se4347.database_system_project.domain.LegInstance;
import com.se4347.database_system_project.domain.Seat;
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
     * Books a seat for a passenger on a specific flight leg instance.
     * Inserts a SEAT row and decrements NO_OF_AVAIL_SEATS on the LEG_INSTANCE.
     */
    @Transactional
    public BookingConfirmation bookSeat(String flightNumber, LocalDate date, int legNo,
                                        String seatNo, String customerName, String customerPhone) {
        // Reject obviously missing inputs before touching the database
        if (flightNumber == null || flightNumber.isBlank()) throw new InvalidInputException("Flight number is required.");
        if (date == null)                                    throw new InvalidInputException("Date is required.");
        if (seatNo == null || seatNo.isBlank())              throw new InvalidInputException("Seat number is required.");
        if (customerName == null || customerName.isBlank())  throw new InvalidInputException("Customer name is required.");
        if (customerPhone == null || customerPhone.isBlank()) throw new InvalidInputException("Customer phone is required.");

        // Normalize so "aa3478" and "12a" are treated identically to "AA3478" and "12A"
        String flight = flightNumber.trim().toUpperCase();
        String seat   = seatNo.trim().toUpperCase();

        // Seat format: 1–2 digit row (1–99) followed by one column letter A–F, e.g. "12A".
        // Enforced here because SEAT_NO is VARCHAR(4) and the DB has no seat-map table to validate against.
        if (!seat.matches("[1-9][0-9]?[A-F]")) {
            throw new InvalidInputException(
                    "Invalid seat '" + seat + "'. Expected row (1–99) + column (A–F), e.g. 12A.");
        }

        // Look up the scheduled occurrence of this flight leg on the given date.
        // LEG_INSTANCE PK is (DATE, NUMBER, LEG_NO) — all three are required to identify one scheduled segment.
        LegInstance.LegInstanceId liId = new LegInstance.LegInstanceId(date, flight, legNo);
        LegInstance li = legInstanceRepository.findById(liId)
                .orElseThrow(() -> new NotFoundException(
                        "No scheduled instance for flight " + flight + " leg " + legNo + " on " + date + "."));

        // Row bounds check using the airplane's seat count as a proxy for the cabin layout.
        // Assumes standard 6-across seating (columns A–F); maxRow = ceil(totalSeats / 6).
        // This is a best-effort guard — the schema stores only a seat count.
        int row = Integer.parseInt(seat.substring(0, seat.length() - 1));
        int maxRow = (int) Math.ceil(li.getAirplane().getTotalNoOfSeats() / 6.0);
        if (row > maxRow) {
            throw new InvalidInputException(
                    "Seat row " + row + " exceeds this aircraft's row capacity (max row " + maxRow
                    + " for " + li.getAirplane().getTotalNoOfSeats() + " seats).");
        }

        // NO_OF_AVAIL_SEATS is decremented on every successful booking (see bottom of this method).
        // If it reaches zero the flight is full and no further bookings are accepted.
        if (li.getNoOfAvailSeats() <= 0) {
            throw new InvalidInputException(
                    "Flight " + flight + " leg " + legNo + " on " + date + " is fully booked.");
        }

        // SEAT PK is (SEAT_NO, DATE, NUMBER, LEG_NO) — unique per physical seat per scheduled leg.
        // existsById just checks existence, without loading full entity.
        Seat.SeatId seatId = new Seat.SeatId(seat, date, flight, legNo);
        if (seatRepository.existsById(seatId)) {
            throw new InvalidInputException(
                    "Seat " + seat + " on " + flight + " leg " + legNo + " (" + date + ") is already reserved.");
        }

        // Insert the reservation row into SEAT. The legInstance association columns
        // (DATE, NUMBER, LEG_NO) are insertable=false — JPA writes them via the embedded SeatId.
        // To ensure referential integrity.
        Seat s = new Seat();
        s.setId(seatId);
        s.setCustomerName(customerName.trim());
        s.setCustomerPhone(customerPhone.trim());
        seatRepository.save(s);

        // Keep the available-seat counter in sync so availability queries and overbooking checks stay accurate.
        li.setNoOfAvailSeats(li.getNoOfAvailSeats() - 1);
        legInstanceRepository.save(li);

        // Both saves above are wrapped in one @Transactional — either both commit or both roll back.
        return new BookingConfirmation(flight, legNo, date, seat, customerName.trim(), customerPhone.trim());
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

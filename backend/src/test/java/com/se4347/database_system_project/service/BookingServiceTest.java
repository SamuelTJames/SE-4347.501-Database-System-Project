package com.se4347.database_system_project.service;

import com.se4347.database_system_project.TestData;
import com.se4347.database_system_project.api.dto.BookingConfirmation;
import com.se4347.database_system_project.api.dto.PassengerItineraryEntry;
import com.se4347.database_system_project.api.dto.SeatAvailability;
import com.se4347.database_system_project.exception.InvalidInputException;
import com.se4347.database_system_project.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceTest {

    @Autowired private BookingService bookingService;
    @Autowired private TestData testData;

    @BeforeEach
    void seed() {
        testData.clearAll();
        testData.seed();
    }

    @Test
    void seatAvailabilityComputesRemainingFromCapacityMinusConfirmed() {
        List<SeatAvailability> rows = bookingService.checkSeatAvailability("AA3478", TestData.FLIGHT_DATE);

        assertThat(rows).hasSize(1);
        SeatAvailability r = rows.get(0);
        assertThat(r.airplaneId()).isEqualTo("N101AA");
        assertThat(r.airplaneCapacity()).isEqualTo(150);
        assertThat(r.confirmedReservations()).isEqualTo(1);
        assertThat(r.remainingSeats()).isEqualTo(149);
    }

    @Test
    void seatAvailabilityHandlesUnbookedLeg() {
        List<SeatAvailability> rows = bookingService.checkSeatAvailability("AA1000", TestData.FLIGHT_DATE);
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).confirmedReservations()).isZero();
        assertThat(rows.get(0).remainingSeats()).isEqualTo(160);
    }

    @Test
    void seatAvailabilityThrowsWhenNoInstanceOnDate() {
        assertThatThrownBy(() -> bookingService.checkSeatAvailability("AA3478", LocalDate.of(2099, 1, 1)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void seatAvailabilityValidatesInputs() {
        assertThatThrownBy(() -> bookingService.checkSeatAvailability(" ", TestData.FLIGHT_DATE))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> bookingService.checkSeatAvailability("AA3478", null))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void passengerItineraryByName() {
        List<PassengerItineraryEntry> entries = bookingService.getPassengerItinerary("jane", null);

        assertThat(entries).hasSize(1);
        PassengerItineraryEntry e = entries.get(0);
        assertThat(e.flightNumber()).isEqualTo("AA3478");
        assertThat(e.legNo()).isEqualTo(1);
        assertThat(e.depAirportCode()).isEqualTo("DFW");
        assertThat(e.arrAirportCode()).isEqualTo("SFO");
        assertThat(e.seatNumber()).isEqualTo("12A");
    }

    @Test
    void passengerItineraryByPhone() {
        List<PassengerItineraryEntry> entries = bookingService.getPassengerItinerary(null, "5551234567");
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).customerName()).isEqualTo("Jane Smith");
    }

    @Test
    void passengerItineraryRequiresExactlyOneCriterion() {
        assertThatThrownBy(() -> bookingService.getPassengerItinerary(null, null))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> bookingService.getPassengerItinerary("Jane", "5551234567"))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void passengerItineraryThrowsForUnknownPassenger() {
        assertThatThrownBy(() -> bookingService.getPassengerItinerary("Nobody", null))
                .isInstanceOf(NotFoundException.class);
    }

    // --- bookSeat ---

    @Test
    void bookSeatCreatesReservationAndDecrementsAvailableSeats() {
        BookingConfirmation c = bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "14B", "John Doe", "5559876543");

        assertThat(c.flightNumber()).isEqualTo("AA3478");
        assertThat(c.legNo()).isEqualTo(1);
        assertThat(c.date()).isEqualTo(TestData.FLIGHT_DATE);
        assertThat(c.seatNo()).isEqualTo("14B");
        assertThat(c.customerName()).isEqualTo("John Doe");
        assertThat(c.customerPhone()).isEqualTo("5559876543");

        // Availability counts from SEAT rows: seeded 12A + new 14B = 2 confirmed, 148 remaining
        SeatAvailability avail = bookingService.checkSeatAvailability("AA3478", TestData.FLIGHT_DATE).get(0);
        assertThat(avail.confirmedReservations()).isEqualTo(2);
        assertThat(avail.remainingSeats()).isEqualTo(148);
    }

    @Test
    void bookSeatNormalisesCaseOnInputs() {
        BookingConfirmation c = bookingService.bookSeat("aa3478", TestData.FLIGHT_DATE, 1, "14b", "John Doe", "5559876543");
        assertThat(c.flightNumber()).isEqualTo("AA3478");
        assertThat(c.seatNo()).isEqualTo("14B");
    }

    @Test
    void bookSeatRejectsSeatAlreadyTaken() {
        // 12A was seeded for Jane Smith
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "12A", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("already reserved");
    }

    @Test
    void bookSeatRejectsDoubleBooingOfSameSeat() {
        bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "14B", "John Doe", "5559876543");
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "14B", "Jane Doe", "5550000000"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("already reserved");
    }

    @Test
    void bookSeatRejectsInvalidSeatFormat() {
        // row starts at 1, not 0
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "0A", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class);
        // column G does not exist in A-F range
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "12G", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class);
        // letter before number is backwards
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "A12", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void bookSeatRejectsSeatRowBeyondAircraftCapacity() {
        // N101AA has 150 seats -> maxRow = ceil(150/6) = 25; row 26 is out of bounds
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "26A", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("row capacity");
    }

    @Test
    void bookSeatThrowsWhenNoLegInstanceOnDate() {
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", LocalDate.of(2099, 1, 1), 1, "14B", "John Doe", "5559876543"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void bookSeatRequiresAllFields() {
        assertThatThrownBy(() -> bookingService.bookSeat(null, TestData.FLIGHT_DATE, 1, "14B", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", null, 1, "14B", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, " ", "John Doe", "5559876543"))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "14B", "", "5559876543"))
                .isInstanceOf(InvalidInputException.class);
        assertThatThrownBy(() -> bookingService.bookSeat("AA3478", TestData.FLIGHT_DATE, 1, "14B", "John Doe", null))
                .isInstanceOf(InvalidInputException.class);
    }
}

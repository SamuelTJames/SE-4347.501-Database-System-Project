package com.se4347.database_system_project.service;

import com.se4347.database_system_project.TestData;
import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.exception.InvalidInputException;
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
class AircraftUtilizationServiceTest {

    @Autowired private AircraftUtilizationService service;
    @Autowired private TestData testData;

    @BeforeEach
    void seed() {
        testData.clearAll();
        testData.seed();
    }

    @Test
    void countsAssignedFlightsPerAirplaneInRange() {
        List<AircraftUtilization> report = service.getUtilizationReport(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));

        // N101 and N102 each carry 1 flight; N103 has none. All three appear.
        assertThat(report).hasSize(3);
        assertThat(report).extracting("airplaneId").containsExactly("N101AA", "N102AA", "N103AA");
        assertThat(report.get(0).totalFlights()).isEqualTo(1L);
        assertThat(report.get(2).totalFlights()).isZero();
    }

    @Test
    void zeroCountWhenRangeOutsideAllInstances() {
        List<AircraftUtilization> report = service.getUtilizationReport(
                LocalDate.of(2030, 1, 1), LocalDate.of(2030, 12, 31));
        assertThat(report).allMatch(r -> r.totalFlights() == 0L);
    }

    @Test
    void rejectsInvertedDateRange() {
        assertThatThrownBy(() -> service.getUtilizationReport(
                LocalDate.of(2026, 5, 31), LocalDate.of(2026, 5, 1)))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void rejectsNullDates() {
        assertThatThrownBy(() -> service.getUtilizationReport(null, LocalDate.now()))
                .isInstanceOf(InvalidInputException.class);
    }
}

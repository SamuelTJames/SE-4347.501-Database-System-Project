package com.se4347.database_system_project.service;

import com.se4347.database_system_project.TestData;
import com.se4347.database_system_project.api.dto.FlightDetails;
import com.se4347.database_system_project.exception.InvalidInputException;
import com.se4347.database_system_project.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class FlightQueryServiceTest {

    @Autowired private FlightQueryService flightQueryService;
    @Autowired private TestData testData;

    @BeforeEach
    void seed() {
        testData.clearAll();
        testData.seed();
    }

    @Test
    void returnsFullDetailsIncludingLegsAndFares() {
        FlightDetails details = flightQueryService.getFlightByNumber("AA3478");

        assertThat(details.number()).isEqualTo("AA3478");
        assertThat(details.airline()).isEqualTo("AA");
        assertThat(details.legs()).hasSize(1);
        assertThat(details.legs().get(0).depAirport().airportCode()).isEqualTo("DFW");
        assertThat(details.legs().get(0).arrAirport().airportCode()).isEqualTo("SFO");
        assertThat(details.fares()).extracting("code").containsExactly("Y", "F");
    }

    @Test
    void normalizesFlightNumberCase() {
        assertThat(flightQueryService.getFlightByNumber("aa3478").number()).isEqualTo("AA3478");
    }

    @Test
    void throwsWhenFlightNumberMissing() {
        assertThatThrownBy(() -> flightQueryService.getFlightByNumber(" "))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void throwsWhenFlightUnknown() {
        assertThatThrownBy(() -> flightQueryService.getFlightByNumber("ZZ0000"))
                .isInstanceOf(NotFoundException.class);
    }
}

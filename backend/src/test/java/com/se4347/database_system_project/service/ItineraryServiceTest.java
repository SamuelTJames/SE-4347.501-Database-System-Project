package com.se4347.database_system_project.service;

import com.se4347.database_system_project.TestData;
import com.se4347.database_system_project.api.dto.ItineraryResults;
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
class ItineraryServiceTest {

    @Autowired private ItineraryService itineraryService;
    @Autowired private TestData testData;

    @BeforeEach
    void seed() {
        testData.clearAll();
        testData.seed();
    }

    @Test
    void findsDirectFlightsByAirportCode() {
        ItineraryResults results = itineraryService.findItineraries("DFW", "SFO");

        assertThat(results.direct()).hasSize(1);
        assertThat(results.direct().get(0).flightNumber()).isEqualTo("AA3478");
    }

    @Test
    void findsOneStopWhenDirectExists() {
        ItineraryResults results = itineraryService.findItineraries("DFW", "SFO");

        assertThat(results.oneStop()).hasSize(1);
        assertThat(results.oneStop().get(0).firstLeg().flightNumber()).isEqualTo("AA1000");
        assertThat(results.oneStop().get(0).secondLeg().flightNumber()).isEqualTo("AA2000");
        assertThat(results.oneStop().get(0).connection().airportCode()).isEqualTo("ATL");
    }

    @Test
    void resolvesByCityName() {
        ItineraryResults results = itineraryService.findItineraries("Dallas", "San Francisco");
        assertThat(results.direct()).extracting("flightNumber").contains("AA3478");
    }

    @Test
    void cityLookupIsCaseInsensitive() {
        ItineraryResults results = itineraryService.findItineraries("dallas", "SAN FRANCISCO");
        assertThat(results.direct()).hasSize(1);
    }

    @Test
    void rejectsSameOriginAndDestination() {
        assertThatThrownBy(() -> itineraryService.findItineraries("DFW", "DFW"))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void throwsWhenAirportUnknown() {
        assertThatThrownBy(() -> itineraryService.findItineraries("DFW", "XYZ"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void returnsEmptyListsWhenNoRouteExists() {
        // JFK has no inbound legs from anywhere except SFO -> JFK on UA9999, so DFW->JFK has neither direct nor one-stop.
        ItineraryResults results = itineraryService.findItineraries("DFW", "JFK");
        assertThat(results.direct()).isEmpty();
        assertThat(results.oneStop()).isEmpty();
    }
}

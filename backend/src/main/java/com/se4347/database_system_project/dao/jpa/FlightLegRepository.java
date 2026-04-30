package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.FlightLeg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightLegRepository extends JpaRepository<FlightLeg, FlightLeg.FlightLegId> {

    // TODO: ItineraryService, BookingService
}

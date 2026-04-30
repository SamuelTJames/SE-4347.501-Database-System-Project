package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, String> {

    // TODO: ItineraryService
}

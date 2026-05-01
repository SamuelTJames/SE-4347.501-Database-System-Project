package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, String> {
}

package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirplaneRepository extends JpaRepository<Airplane, String> {

    // TODO: AircraftUtilizationService
}

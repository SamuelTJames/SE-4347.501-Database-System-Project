package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.LegInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegInstanceRepository extends JpaRepository<LegInstance, LegInstance.LegInstanceId> {

    // TODO: AircraftUtilizationService, BookingService
}

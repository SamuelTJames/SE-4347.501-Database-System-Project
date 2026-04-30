package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.AirplaneType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirplaneTypeRepository extends JpaRepository<AirplaneType, String> {

    // TODO: BookingService
}

package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.Fare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FareRepository extends JpaRepository<Fare, Fare.FareId> {

    // TODO: FlightQueryService
}

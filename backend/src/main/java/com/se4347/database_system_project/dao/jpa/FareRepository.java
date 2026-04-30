package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FareRepository extends JpaRepository<Fare, Fare.FareId> {

    @Query("""
            SELECT f FROM Fare f
            WHERE f.id.flightNumber = :flightNumber
            ORDER BY f.amount ASC, f.id.code ASC
            """)
    List<Fare> findByFlightNumber(@Param("flightNumber") String flightNumber);
}

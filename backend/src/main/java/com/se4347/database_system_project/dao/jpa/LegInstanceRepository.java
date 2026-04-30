package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.LegInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LegInstanceRepository extends JpaRepository<LegInstance, LegInstance.LegInstanceId> {

    /**
     * Returns the leg instances for every leg of a given flight on a given date.
     */
    @Query("""
            SELECT li FROM LegInstance li
            JOIN FETCH li.airplane a
            JOIN FETCH a.airplaneType
            JOIN FETCH li.flightLeg fl
            WHERE li.id.date = :date
              AND li.id.flightNumber = :flightNumber
            ORDER BY li.id.legNo ASC
            """)
    List<LegInstance> findInstancesForFlight(@Param("flightNumber") String flightNumber,
                                             @Param("date") LocalDate date);
}

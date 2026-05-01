package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.api.dto.AircraftUtilization;
import com.se4347.database_system_project.domain.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AirplaneRepository extends JpaRepository<Airplane, String> {

    /**
     * Per-airplane utilization for a date range. The correlated subquery returns 0 for
     * airplanes with no assigned leg instances, so idle airplanes still appear in the
     * report. The result is sorted in the service so the ORDER BY can stay simple here.
     */
    @Query("""
            SELECT new com.se4347.database_system_project.api.dto.AircraftUtilization(
                a.airplaneId,
                a.airplaneType.typeName,
                (SELECT COUNT(li) FROM LegInstance li
                  WHERE li.airplane = a AND li.id.date BETWEEN :start AND :end)
            )
            FROM Airplane a
            ORDER BY a.airplaneId ASC
            """)
    List<AircraftUtilization> findUtilizationReport(@Param("start") LocalDate start,
                                                    @Param("end") LocalDate end);
}

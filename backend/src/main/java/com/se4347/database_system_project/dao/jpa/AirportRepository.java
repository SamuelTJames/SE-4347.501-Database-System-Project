package com.se4347.database_system_project.dao.jpa;

import com.se4347.database_system_project.domain.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, String> {

    @Query("SELECT a FROM Airport a WHERE UPPER(a.airportCode) = UPPER(:code)")
    Optional<Airport> findByCodeIgnoreCase(@Param("code") String code);

    @Query("""
            SELECT a FROM Airport a
            WHERE LOWER(a.city) = LOWER(:city)
            ORDER BY a.airportCode ASC
            """)
    List<Airport> findByCityIgnoreCase(@Param("city") String city);
}

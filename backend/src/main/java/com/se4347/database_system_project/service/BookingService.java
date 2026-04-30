package com.se4347.database_system_project.service;

import com.se4347.database_system_project.dao.jpa.AirplaneTypeRepository;
import com.se4347.database_system_project.dao.jpa.LegInstanceRepository;
import com.se4347.database_system_project.dao.jpa.SeatRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final LegInstanceRepository legInstanceRepository;
    private final SeatRepository seatRepository;
    private final AirplaneTypeRepository airplaneTypeRepository;

    public BookingService(LegInstanceRepository legInstanceRepository,
                          SeatRepository seatRepository,
                          AirplaneTypeRepository airplaneTypeRepository) {
        this.legInstanceRepository = legInstanceRepository;
        this.seatRepository = seatRepository;
        this.airplaneTypeRepository = airplaneTypeRepository;
    }

    // TODO: checkSeatAvailability
    // TODO: getPassengerItinerary
}

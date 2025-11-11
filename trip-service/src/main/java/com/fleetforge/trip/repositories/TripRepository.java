package com.fleetforge.trip.repositories;

import com.fleetforge.trip.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip,Long> {
}

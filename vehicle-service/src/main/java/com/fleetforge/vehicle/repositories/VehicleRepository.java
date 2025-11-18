package com.fleetforge.vehicle.repositories;

import com.fleetforge.vehicle.entities.Vehicle;
import com.fleetforge.vehicle.entities.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findById(Long Id);

    List<Vehicle> findByStatus(VehicleStatus status);

    boolean existsByRegistrationNumber(String registrationNumber);
}

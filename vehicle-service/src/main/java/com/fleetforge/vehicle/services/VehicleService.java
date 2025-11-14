package com.fleetforge.vehicle.services;

import com.fleetforge.vehicle.entities.Vehicle;
import com.fleetforge.vehicle.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;


    public Vehicle addVehicle(Vehicle vehicle) {
         validateRegistration(vehicle.getRegistrationNumber());
        if (vehicleRepository.existsByRegistrationNumber(vehicle.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        validateRegistration(vehicle.getRegistrationNumber());

        if (!existing.getRegistrationNumber().equals(vehicle.getRegistrationNumber()) &&
                vehicleRepository.existsByRegistrationNumber(vehicle.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }

        existing.setRegistrationNumber(vehicle.getRegistrationNumber());
        existing.setModel(vehicle.getModel());
        existing.setCapacity(vehicle.getCapacity());
        existing.setStatus(vehicle.getStatus());

        return vehicleRepository.save(existing);
    }


    public boolean deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) return false;
        vehicleRepository.deleteById(id);
        return true;
    }

    public Vehicle assignVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available");
        }

        vehicle.setStatus(Vehicle.Status.ON_TRIP);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle releaseVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setStatus(Vehicle.Status.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
    }

    private void validateRegistration(String reg) {
        if (reg == null || !reg.matches("^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$")) {
            throw new RuntimeException("Invalid registration number format");
        }
    }


}

package com.fleetforge.vehicle.controllers;

import com.fleetforge.vehicle.entities.Vehicle;
import com.fleetforge.vehicle.entities.VehicleStatus;
import com.fleetforge.vehicle.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.addVehicle(vehicle));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehiclesById(@PathVariable("id") Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(vehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable("id") Long id, @RequestBody Vehicle vehicle) {
        Vehicle updated = vehicleService.updateVehicle(id, vehicle);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable("id") Long id) {
        boolean deleted = vehicleService.deleteVehicle(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Vehicle with id " + id + " is deleted successfully");
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Vehicle> assignVehicle(@PathVariable("id") Long id) {
        Vehicle vehicle = vehicleService.assignVehicle(id);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<Vehicle> releaseVehicle(@PathVariable("id") Long id) {
        Vehicle vehicle = vehicleService.releaseVehicle(id);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Vehicle>> getVehiclesByStatus(@PathVariable("status") VehicleStatus status) {
        List<Vehicle> vehicles = vehicleService.getAvailableVehicles(); // fetch by status
        return ResponseEntity.ok(vehicles);
    }
}

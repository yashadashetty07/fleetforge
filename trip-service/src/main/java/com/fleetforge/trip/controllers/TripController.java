package com.fleetforge.trip.controllers;

import com.fleetforge.trip.entities.Trip;
import com.fleetforge.trip.entities.TripStatus;
import com.fleetforge.trip.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    // -------------------- CREATE TRIP (ADMIN) --------------------
    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody Trip tripRequest) {
        Trip saved = tripService.createTrip(tripRequest);
        return ResponseEntity.ok(saved);
    }

    // -------------------- GET TRIPS BY DRIVER --------------------
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Trip>> getTripsByDriver(@PathVariable("driverId") Long driverId) {
        return ResponseEntity.ok(tripService.getTripsByDriver(driverId));
    }

    // -------------------- GET ALL TRIPS (ADMIN) --------------------
    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    // -------------------- GET TRIP BY ID --------------------
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    // -------------------- UPDATE STATUS (NOT USED IN WORKFLOW) --------------------
    @PutMapping("/{id}/status")
    public ResponseEntity<Trip> updateTripStatus(@PathVariable("id") Long id,
                                                 @RequestParam("status") TripStatus status) {
        Trip updated = tripService.updateTripStatus(id, status);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // -------------------- DELETE TRIP --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable("id") Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok("Trip deleted successfully");
    }

    // -------------------- START TRIP (DRIVER ONLY) --------------------
    @PutMapping("/{id}/start")
    public ResponseEntity<Trip> startTrip(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Name") String username) {

        Trip updatedTrip = tripService.startTrip(id, username);
        return ResponseEntity.ok(updatedTrip);
    }

    // -------------------- COMPLETE TRIP (DRIVER ONLY) --------------------
    @PutMapping("/{id}/complete")
    public ResponseEntity<Trip> completeTrip(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Name") String username) {

        Trip updatedTrip = tripService.completeTrip(id, username);
        return ResponseEntity.ok(updatedTrip);
    }

    // -------------------- SUMMARY: DRIVER --------------------
    @GetMapping("/summary/driver/{driverId}")
    public ResponseEntity<Map<String, Object>> getDriverTripSummary(@PathVariable Long driverId) {
        Map<String, Object> summary = tripService.getDriverTripSummary(driverId);
        return ResponseEntity.ok(summary);
    }

    // -------------------- SUMMARY: VEHICLE --------------------
    @GetMapping("/summary/vehicle/{vehicleId}")
    public ResponseEntity<Map<String, Object>> getVehicleTripSummary(@PathVariable Long vehicleId) {
        Map<String, Object> summary = tripService.getVehicleTripSummary(vehicleId);
        return ResponseEntity.ok(summary);
    }

    // -------------------- SUMMARY: ALL --------------------
    @GetMapping("/summary/all")
    public ResponseEntity<Map<String, Object>> getAllTripsSummary() {
        Map<String, Object> summary = tripService.getAllTripsSummary();
        return ResponseEntity.ok(summary);
    }
}

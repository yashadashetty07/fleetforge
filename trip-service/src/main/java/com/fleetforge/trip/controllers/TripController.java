package com.fleetforge.trip.controllers;

import com.fleetforge.trip.entities.Trip;
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

    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody Trip tripRequest) {
        Trip saved = tripService.createTrip(tripRequest);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Trip>> getTripsByDriver(@PathVariable("id") Long driverId) {
        return ResponseEntity.ok(tripService.getTripsByDriver(driverId));
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Trip> updateTripStatus(@PathVariable("id") Long id,
                                                 @RequestParam("status") Trip.Status status) {
        Trip updated = tripService.updateTripStatus(id, status);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable("id") Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok("Trip deleted successfully");
    }
    @PutMapping("/{id}/start")
    public ResponseEntity<Trip> startTrip(@PathVariable("id") Long id) {
        Trip updatedTrip = tripService.startTrip(id);
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Trip> completeTrip(@PathVariable("id") Long id) {
        Trip updatedTrip = tripService.completeTrip(id);
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Trip> cancelTrip(@PathVariable("id") Long id) {
        Trip updatedTrip = tripService.cancelTrip(id);
        return ResponseEntity.ok(updatedTrip);
    }

    @GetMapping("/summary/driver/{driverId}")
    public ResponseEntity<Map<String, Object>> getDriverTripSummary(@PathVariable Long driverId) {
        Map<String, Object> summary = tripService.getDriverTripSummary(driverId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/vehicle/{vehicleId}")
    public ResponseEntity<Map<String, Object>> getVehicleTripSummary(@PathVariable Long vehicleId) {
        Map<String, Object> summary = tripService.getVehicleTripSummary(vehicleId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/all")
    public ResponseEntity<Map<String, Object>> getAllTripsSummary() {
        Map<String, Object> summary = tripService.getAllTripsSummary();
        return ResponseEntity.ok(summary);
    }


}

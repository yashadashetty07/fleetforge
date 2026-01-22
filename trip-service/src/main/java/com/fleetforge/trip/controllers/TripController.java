package com.fleetforge.trip.controllers;

import com.fleetforge.trip.entities.Trip;
import com.fleetforge.trip.entities.TripStatus;
import com.fleetforge.trip.services.DriverServiceClient;
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

    @Autowired
    private DriverServiceClient driverServiceClient;

    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody Trip tripRequest) {
        Trip saved = tripService.createTrip(tripRequest);
        return ResponseEntity.ok(saved);
    }
//
//    @GetMapping("/driver/{driverId}")
//    public ResponseEntity<List<Trip>> getTripsByDriver(@PathVariable("driverId") Long driverId) {
//        return ResponseEntity.ok(tripService.getTripsByDriver(driverId));
//    }

    @GetMapping("/my")
    public ResponseEntity<List<Trip>> getMyTrips(
            @RequestHeader("X-User-Name") String username) {
        Long driverId = driverServiceClient.getDriverIdByEmail(username);
        return ResponseEntity.ok(tripService.getTripsByDriver(driverId));
    }

    @GetMapping("/my/active")
    public ResponseEntity<Trip> getMyActiveTrip(
            @RequestHeader("X-User-Name") String username) {

        Long driverId = driverServiceClient.getDriverIdByEmail(username);
        return tripService.getTripsByDriver(driverId).stream()
                .filter(t -> t.getStatus() == TripStatus.IN_PROGRESS)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(null));
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
                                                 @RequestParam("status") TripStatus status) {
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
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(
            @PathVariable Long id,
            @RequestBody Trip tripRequest
    ) {
        Trip updated = tripService.updateTrip(id, tripRequest);
        return ResponseEntity.ok(updated);
    }

}

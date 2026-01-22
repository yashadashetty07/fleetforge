package com.fleetforge.trip.services;

import com.fleetforge.trip.entities.Trip;
import com.fleetforge.trip.entities.TripStatus;
import com.fleetforge.trip.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteServiceClient routeServiceClient;

    @Autowired
    private VehicleServiceClient vehicleClient;

    @Autowired
    private DriverServiceClient driverClient;   // needed to convert username → driverId


    public Trip createTrip(Trip trip) {
        RouteServiceClient.RouteInfo info =
                routeServiceClient.calculateRoute(trip.getOrigin(), trip.getDestination());

        if (info != null) {
            trip.setOriginLat(info.getOriginLat());
            trip.setOriginLng(info.getOriginLng());
            trip.setDestinationLat(info.getDestinationLat());
            trip.setDestinationLng(info.getDestinationLng());
            trip.setDistance(info.getDistance());
            trip.setEta(info.getEta());
        }

        trip.setStatus(TripStatus.PENDING);
        System.out.println("RouteInfo = " + info);
        return tripRepository.save(trip);
    }


    public List<Trip> getTripsByDriver(Long driverId) {
        return tripRepository.findByDriverId(driverId);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id:" + id));
    }

    public Trip updateTrip(Long id, Trip updatedData) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getStatus() != TripStatus.PENDING) {
            throw new RuntimeException("Only pending trips can be updated");
        }

        trip.setOrigin(updatedData.getOrigin());
        trip.setDestination(updatedData.getDestination());
        trip.setDriverId(updatedData.getDriverId());
        trip.setVehicleId(updatedData.getVehicleId());

        return tripRepository.save(trip);
    }

    // -------------------- BASIC UPDATE (not used in workflow) --------------------
    public Trip updateTripStatus(Long tripId, TripStatus status) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) return null;

        Trip trip = optionalTrip.get();
        trip.setStatus(status);
        return tripRepository.save(trip);
    }


    // -------------------- DELETE --------------------
    public void deleteTrip(Long id) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        tripRepository.delete(existingTrip);
    }


    // -------------------- START TRIP --------------------
    public Trip startTrip(Long tripId, String usernameFromToken) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Convert username → driverId
        Long driverIdFromToken = driverClient.getDriverIdByEmail(usernameFromToken);

        // Validation: only assigned driver can start
        if (!trip.getDriverId().equals(driverIdFromToken)) {
            throw new RuntimeException("You are not assigned to this trip");
        }

        // Validation: must be pending
        if (trip.getStatus() != TripStatus.PENDING) {
            throw new RuntimeException("Trip cannot be started in current state");
        }

        // Update vehicle status → BUSY
        vehicleClient.assignVehicle(trip.getVehicleId());

        // Mark trip as in progress
        trip.setStatus(TripStatus.IN_PROGRESS);

        return tripRepository.save(trip);
    }


    // -------------------- COMPLETE TRIP --------------------
    public Trip completeTrip(Long tripId, String usernameFromToken) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Convert username → driverId
        Long driverIdFromToken = driverClient.getDriverIdByEmail(usernameFromToken);

        // Validation: only assigned driver can complete
        if (!trip.getDriverId().equals(driverIdFromToken)) {
            throw new RuntimeException("You are not assigned to this trip");
        }

        // Validation: must be in progress
        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new RuntimeException("Trip cannot be completed now");
        }

        // Update vehicle status → AVAILABLE
        vehicleClient.releaseVehicle(trip.getVehicleId());

        // Mark trip completed
        trip.setStatus(TripStatus.COMPLETED);

        return tripRepository.save(trip);
    }


    // -------------------- SUMMARY METHODS --------------------
    public Map<String, Object> getDriverTripSummary(Long driverId) {
        List<Trip> trips = tripRepository.findByDriverId(driverId);
        double totalDistance = trips.stream().mapToDouble(Trip::getDistance).sum();
        double totalEta = trips.stream().mapToDouble(Trip::getEta).sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("driverId", driverId);
        summary.put("tripCount", trips.size());
        summary.put("totalDistance", totalDistance);
        summary.put("totalEta", totalEta);
        return summary;
    }

    public Map<String, Object> getVehicleTripSummary(Long vehicleId) {
        List<Trip> trips = tripRepository.findByVehicleId(vehicleId);
        double totalDistance = trips.stream().mapToDouble(Trip::getDistance).sum();
        double totalEta = trips.stream().mapToDouble(Trip::getEta).sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("vehicleId", vehicleId);
        summary.put("tripCount", trips.size());
        summary.put("totalDistance", totalDistance);
        summary.put("totalEta", totalEta);
        return summary;
    }

    public Map<String, Object> getAllTripsSummary() {
        List<Trip> trips = tripRepository.findAll();
        double totalDistance = trips.stream().mapToDouble(Trip::getDistance).sum();
        double totalEta = trips.stream().mapToDouble(Trip::getEta).sum();
        double avgEta = trips.isEmpty() ? 0.0 : totalEta / trips.size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTrips", trips.size());
        summary.put("totalDistance", totalDistance);
        summary.put("averageEta", avgEta);
        return summary;
    }

    public Map<String, Object> getOverallTripSummary() {
        List<Trip> trips = tripRepository.findAll();
        double totalDistance = trips.stream().mapToDouble(Trip::getDistance).sum();
        double totalEta = trips.stream().mapToDouble(Trip::getEta).sum();

        Map<Long, Long> tripsPerDriver = trips.stream()
                .collect(Collectors.groupingBy(Trip::getDriverId, Collectors.counting()));
        Map<Long, Long> tripsPerVehicle = trips.stream()
                .collect(Collectors.groupingBy(Trip::getVehicleId, Collectors.counting()));

        return Map.of(
                "totalTrips", trips.size(),
                "totalDistance", totalDistance,
                "totalEta", totalEta,
                "tripsPerDriver", tripsPerDriver,
                "tripsPerVehicle", tripsPerVehicle
        );
    }

}

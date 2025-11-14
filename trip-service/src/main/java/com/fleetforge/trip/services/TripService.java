package com.fleetforge.trip.services;

import com.fleetforge.trip.entities.Trip;
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

    public Trip createTrip(Trip trip) {
        // call route service by origin/destination strings
        RouteServiceClient.RouteInfo info = routeServiceClient.calculateRoute(trip.getOrigin(), trip.getDestination());

        if (info != null) {
            trip.setOriginLat(info.getOriginLat());
            trip.setOriginLng(info.getOriginLng());
            trip.setDestinationLat(info.getDestinationLat());
            trip.setDestinationLng(info.getDestinationLng());
            trip.setDistance(info.getDistance());
            trip.setEta(info.getEta());
        } // if null, keep whatever caller provided or zeros; optional: throw error

        trip.setStatus(Trip.Status.SCHEDULED);
        return tripRepository.save(trip);
    }

    public List<Trip> getTripsByDriver(Long driverId) {
        return tripRepository.findByDriverId(driverId);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found with id:" + id));
    }

    public Trip updateTripStatus(Long tripId, Trip.Status status) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) return null;
        Trip trip = optionalTrip.get();
        trip.setStatus(status);
        return tripRepository.save(trip);
    }

    public void deleteTrip(Long id) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
        tripRepository.delete(existingTrip);
    }

    public Trip startTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(Trip.Status.valueOf("IN_PROGRESS"));
        return tripRepository.save(trip);
    }

    public Trip completeTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(Trip.Status.valueOf("COMPLETED"));
        return tripRepository.save(trip);
    }

    public Trip cancelTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(Trip.Status.valueOf("CANCELLED"));
        return tripRepository.save(trip);
    }
// -------------------- TRIP SUMMARY METHODS --------------------

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
    // TripService.java

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

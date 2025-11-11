package com.fleetforge.trip.services;

import com.fleetforge.trip.entities.Trip;
import com.fleetforge.trip.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    public Trip createTrip (Trip trip){
        return  tripRepository.save(trip);
    }

    public List<Trip> getAllTrips(){
        return tripRepository.findAll();
    }

    public Trip getTripById(Long id){
        return tripRepository.findById(id).orElseThrow(()->new RuntimeException("Trip not found with id:"+id));
    }

    public Trip updateTrip(Long id, Trip updatedTrip) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

        existingTrip.setName(updatedTrip.getName());
        existingTrip.setVehicleId(updatedTrip.getVehicleId());
        existingTrip.setDriverId(updatedTrip.getDriverId());
        existingTrip.setRouteId(updatedTrip.getRouteId());
        existingTrip.setStartDate(updatedTrip.getStartDate());
        existingTrip.setEndDate(updatedTrip.getEndDate());
        existingTrip.setDistanceKm(updatedTrip.getDistanceKm());
        existingTrip.setEstimatedDurationMinutes(updatedTrip.getEstimatedDurationMinutes());

        return tripRepository.save(existingTrip);
    }

    public void deleteTrip(Long id) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
        tripRepository.delete(existingTrip);
    }


}

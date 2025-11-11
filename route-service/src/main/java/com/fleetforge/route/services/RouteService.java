package com.fleetforge.route.services;

import com.fleetforge.route.entities.Route;
import com.fleetforge.route.repositories.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {


    @Autowired
    private RouteRepository routeRepository;


    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id).orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route updateRoute(Long id, Route updatedRoute) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));

        existingRoute.setName(updatedRoute.getName());
        existingRoute.setSource(updatedRoute.getSource());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setDistanceKm(updatedRoute.getDistanceKm());
        existingRoute.setEstimatedDurationMinutes(updatedRoute.getEstimatedDurationMinutes());

        return routeRepository.save(existingRoute);
    }

    public void deleteRoute(Long id) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        routeRepository.delete(existingRoute);
    }
}


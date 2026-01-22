package com.fleetforge.route.services;

import com.fleetforge.route.entities.Route;
import com.fleetforge.route.repositories.RouteRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private MapboxService mapboxService;

    public Route createRoute(Route route) {
        try {
            // Step 1: Geocode both locations
            JSONObject sourceGeo = mapboxService.geocodeAddress(route.getSource());
            JSONObject destGeo = mapboxService.geocodeAddress(route.getDestination());

            JSONObject sourceFeature = sourceGeo.getJSONArray("features").getJSONObject(0);
            double originLng = sourceFeature.getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
            double originLat = sourceFeature.getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);

            JSONObject destFeature = destGeo.getJSONArray("features").getJSONObject(0);
            double destLng = destFeature.getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
            double destLat = destFeature.getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);

            // Step 2: Fetch distance and duration
            double[] result = mapboxService.getDistanceAndDuration(originLng, originLat, destLng, destLat);

            // Step 3: Populate calculated fields
            route.setSourceLat(originLat);
            route.setSourceLng(originLng);
            route.setDestinationLat(destLat);
            route.setDestinationLng(destLng);
            route.setDistanceKm(result[0]);
            route.setEstimatedDurationMinutes(result[1]);

        } catch (Exception e) {
            System.out.println(" Mapbox auto-calculation failed: " + e.getMessage());
            route.setDistanceKm(0.0);
            route.setEstimatedDurationMinutes(0.0);
        }

        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
    }

    public Route updateRoute(Long id, Route updatedRoute) {
        Route existing = getRouteById(id);

        existing.setSource(updatedRoute.getSource());
        existing.setDestination(updatedRoute.getDestination());
        existing.setSourceLat(updatedRoute.getSourceLat());
        existing.setSourceLng(updatedRoute.getSourceLng());
        existing.setDestinationLat(updatedRoute.getDestinationLat());
        existing.setDestinationLng(updatedRoute.getDestinationLng());

        if (updatedRoute.getSourceLat() != null && updatedRoute.getSourceLng() != null
                && updatedRoute.getDestinationLat() != null && updatedRoute.getDestinationLng() != null) {

            double[] result = mapboxService.getDistanceAndDuration(
                    updatedRoute.getSourceLng(),
                    updatedRoute.getSourceLat(),
                    updatedRoute.getDestinationLng(),
                    updatedRoute.getDestinationLat()
            );

            existing.setDistanceKm(result[0]);
            existing.setEstimatedDurationMinutes(result[1]);
        }

        return routeRepository.save(existing);
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }
}

package com.fleetforge.route.controllers;

import com.fleetforge.route.entities.Route;
import com.fleetforge.route.services.MapboxService;
import com.fleetforge.route.services.RouteService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private MapboxService mapboxService;

    // -------------------------
    // CRUD Endpoints
    // -------------------------
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        // TODO: integrate Mapbox API to auto-fill distance and ETA later
        Route savedRoute = routeService.createRoute(route);
        return ResponseEntity.ok(savedRoute);
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route route) {
        return ResponseEntity.ok(routeService.updateRoute(id, route));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok("Route " + id + " deleted successfully");
    }

    // -------------------------
    // Mapbox Integration
    // -------------------------
    @GetMapping("/distance")
    public ResponseEntity<Map<String, Object>> getDistanceAndDuration(
            @RequestParam double originLng,
            @RequestParam double originLat,
            @RequestParam double destLng,
            @RequestParam double destLat) {

        double[] result = mapboxService.getDistanceAndDuration(originLng, originLat, destLng, destLat);
        Map<String, Object> response = Map.of(
                "distance_km", result[0],
                "duration_minutes", result[1]
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calculate")
    public ResponseEntity<String> calculateRoute(
            @RequestParam(name = "originLng") double originLng,
            @RequestParam(name = "originLat") double originLat,
            @RequestParam(name = "destLng") double destLng,
            @RequestParam(name = "destLat") double destLat) {
        return ResponseEntity.ok(mapboxService.getDirections(originLng, originLat, destLng, destLat).toString());
    }

    @GetMapping("/geocode")
    public ResponseEntity<String> geocodeAddress(@RequestParam String address) {
        return ResponseEntity.ok(mapboxService.geocodeAddress(address).toString());
    }

    @GetMapping("/calculateByName")
    public ResponseEntity<Map<String, Object>> calculateByName(
            @RequestParam String origin,
            @RequestParam String destination) {

        try {
            // Step 1: Geocode both locations
            JSONObject sourceGeo = mapboxService.geocodeAddress(origin);
            JSONObject destGeo = mapboxService.geocodeAddress(destination);

            double originLng = sourceGeo.getJSONArray("features").getJSONObject(0)
                    .getJSONArray("center").getDouble(0);
            double originLat = sourceGeo.getJSONArray("features").getJSONObject(0)
                    .getJSONArray("center").getDouble(1);
            double destLng = destGeo.getJSONArray("features").getJSONObject(0)
                    .getJSONArray("center").getDouble(0);
            double destLat = destGeo.getJSONArray("features").getJSONObject(0)
                    .getJSONArray("center").getDouble(1);

            double[] result = mapboxService.getDistanceAndDuration(originLng, originLat, destLng, destLat);

            Map<String, Object> response = Map.of(
                    "originCoordinates", List.of(originLng, originLat),
                    "destinationCoordinates", List.of(destLng, destLat),
                    "distance", result[0],
                    "eta", result[1]
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Route calculation failed: " + e.getMessage()));
        }
    }

}

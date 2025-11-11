package com.fleetforge.route.controllers;

import com.fleetforge.route.entities.Route;
import com.fleetforge.route.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route savedRoute = routeService.createRoute(route);
        return ResponseEntity.ok(savedRoute);
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable("id") Long id) {
        Route route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable("id") Long id, @RequestBody Route route) {
        Route updatedRoute = routeService.updateRoute(id, route);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable("id") Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok("Route "+id+" deleted successfully");
    }
}

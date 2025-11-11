package com.fleetforge.route.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fleetforge.route.entities.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
}

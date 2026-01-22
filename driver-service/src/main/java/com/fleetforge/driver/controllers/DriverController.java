package com.fleetforge.driver.controllers;

import com.fleetforge.driver.entities.Driver;
import com.fleetforge.driver.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping
    public ResponseEntity<?> addDriver(@RequestBody Driver driver) {
        Map<String, Object> response = driverService.addDriver(driver);
        return ResponseEntity.ok(response);
    }



    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable("id") Long id) {
        Driver driver = driverService.getDriverById(id);
        if (driver == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(driver);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable("id") Long id, @RequestBody Driver driver) {
        Driver updated = driverService.updateDriver(id, driver);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable("id") Long id) {
        boolean deleted = driverService.deleteDriver(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Driver with id " +id+" deleted successfully");
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Driver> getDriverByUsername(@PathVariable("email") String username) {
        Driver d = driverService.getDriverByUsername(username);
        if (d == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(d);
    }

}

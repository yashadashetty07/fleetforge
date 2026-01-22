package com.fleetforge.driver.services;

import com.fleetforge.driver.entities.Driver;
import com.fleetforge.driver.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AuthServiceClient authServiceClient;

    public Map<String, Object> addDriver(Driver driver) {
        Driver saved = driverRepository.save(driver);

        if (driver.getEmail() == null || driver.getEmail().isBlank()) {
            throw new RuntimeException("Driver email is required");
        }

        String username = driver.getEmail();
        String tempPassword = authServiceClient.createDriverUser(username);

        // return driver + temp password to frontend
        return Map.of(
                "driver", saved,
                "username", username,
                "tempPassword", tempPassword
        );
    }




    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id).orElse(null);
    }

    public Driver updateDriver(Long id, Driver updatedDriver) {
        Driver driver = driverRepository.findById(id).orElse(null);
        if (driver == null) return null;

        driver.setName(updatedDriver.getName());
        driver.setLicenseNumber(updatedDriver.getLicenseNumber());
        driver.setPhone(updatedDriver.getPhone());
        driver.setEmail(updatedDriver.getEmail());
        driver.setStatus(updatedDriver.getStatus());

        return driverRepository.save(driver);
    }

    public boolean deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) return false;
        driverRepository.deleteById(id);
        return true;
    }
    public Driver getDriverByUsername(String email) {
        return driverRepository.findByEmail(email);
    }


}

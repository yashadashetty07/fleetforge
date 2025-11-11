package com.fleetforge.driver.services;

import com.fleetforge.driver.entities.Driver;
import com.fleetforge.driver.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public Driver addDriver(Driver driver) {
        return driverRepository.save(driver);
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
}

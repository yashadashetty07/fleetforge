package com.fleetforge.trip.services;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DriverServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    // Adjust the port if your driver-service is running on different one
    private static final String BASE_URL = "http://localhost:8083/api/drivers";

    // This calls: GET /api/drivers/username/{username}
    public Long getDriverIdByUsername(String username) {

        try {
            DriverResponse driver =
                    restTemplate.getForObject(
                            BASE_URL + "/username/" + username,
                            DriverResponse.class);

            if (driver == null) {
                throw new RuntimeException("Driver not found with username: " + username);
            }

            return driver.getId();

        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve driverId for username: " + username);
        }
    }

    // Small inner DTO to receive the driver response
    private static class DriverResponse {
        private Long id;
        private String name;
        private String licenseNumber;
        private String phone;
        private String email;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}

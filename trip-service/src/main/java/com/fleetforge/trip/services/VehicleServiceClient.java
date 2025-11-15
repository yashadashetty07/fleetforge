package com.fleetforge.trip.services;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class VehicleServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8082/api/vehicles";

    public void assignVehicle(Long vehicleId) {
        restTemplate.postForObject(BASE_URL + "/" + vehicleId + "/assign", null, Void.class);
    }

    public void releaseVehicle(Long vehicleId) {
        restTemplate.postForObject(BASE_URL + "/" + vehicleId + "/release", null, Void.class);
    }
}

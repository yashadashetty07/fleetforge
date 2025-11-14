package com.fleetforge.trip.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class RouteServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public RouteServiceClient(@Value("${route.service.url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public RouteInfo calculateRoute(String origin, String destination) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/api/routes/calculateByName")
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return null;

            return parseRouteInfo(response);
        } catch (Exception e) {
            // Optionally log the error
            return null;
        }
    }

    private RouteInfo parseRouteInfo(Map<String, Object> response) {
        List<?> originCoords = (List<?>) response.get("originCoordinates");
        List<?> destCoords = (List<?>) response.get("destinationCoordinates");

        double originLng = toDouble(originCoords.get(0));
        double originLat = toDouble(originCoords.get(1));
        double destLng = toDouble(destCoords.get(0));
        double destLat = toDouble(destCoords.get(1));

        double distance = toDouble(response.get("distance"));
        double eta = toDouble(response.get("eta"));

        return new RouteInfo(originLat, originLng, destLat, destLng, distance, eta);
    }

    private double toDouble(Object obj) {
        return obj instanceof Number ? ((Number) obj).doubleValue() : 0.0;
    }

    public static class RouteInfo {
        private final double originLat;
        private final double originLng;
        private final double destinationLat;
        private final double destinationLng;
        private final double distance;
        private final double eta;

        public RouteInfo(double originLat, double originLng,
                         double destinationLat, double destinationLng,
                         double distance, double eta) {
            this.originLat = originLat;
            this.originLng = originLng;
            this.destinationLat = destinationLat;
            this.destinationLng = destinationLng;
            this.distance = distance;
            this.eta = eta;
        }

        public double getOriginLat() { return originLat; }
        public double getOriginLng() { return originLng; }
        public double getDestinationLat() { return destinationLat; }
        public double getDestinationLng() { return destinationLng; }
        public double getDistance() { return distance; }
        public double getEta() { return eta; }
    }
}

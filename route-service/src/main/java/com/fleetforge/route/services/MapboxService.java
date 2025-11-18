package com.fleetforge.route.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MapboxService {

    private final RestTemplate restTemplate;

    @Value("${mapbox.api.key}")
    private String apiKey;

    @Value("${mapbox.api.url}")
    private String apiUrl; // e.g. https://api.mapbox.com/directions/v5/mapbox/driving

    @Value("${mapbox.geocode.url}")
    private String geocodeUrl; // e.g. https://api.mapbox.com/geocoding/v5/mapbox.places

    public MapboxService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double[] getDistanceAndDuration(double originLng, double originLat, double destLng, double destLat) {
        try {
            String coordinates = String.format("%f,%f;%f,%f", originLng, originLat, destLng, destLat);
            String url = String.format("%s/%sjson?country=IN&limit=5&access_token=%s&geometries=geojson", apiUrl, coordinates, apiKey);

            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);

            double distance = json.getJSONArray("routes")
                    .getJSONObject(0)
                    .getDouble("distance") / 1000.0;

            double duration = json.getJSONArray("routes")
                    .getJSONObject(0)
                    .getDouble("duration") / 60.0;

            return new double[]{distance, duration};

        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{0, 0};
        }
    }

    public JSONObject getDirections(double originLng, double originLat, double destLng, double destLat) {
        try {
            String coordinates = String.format("%f,%f;%f,%f", originLng, originLat, destLng, destLat);
            String url = String.format("%s/%sjson?country=IN&limit=5?access_token=%s&geometries=geojson", apiUrl, coordinates, apiKey);

            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response);

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Failed to fetch directions");
        }
    }

    public JSONObject geocodeAddress(String address) {
        try {
            String encodedAddress = address.replace(" ", "%20");
            String url = String.format("%s/%s.json?country=IN&limit=5access_token=%s", geocodeUrl, encodedAddress, apiKey);

            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response);

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Failed to geocode address");
        }
    }
}

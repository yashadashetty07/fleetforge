package com.fleetforge.route.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MapboxService {

    private final RestTemplate restTemplate;

    @Value("${mapbox.api.key}")
    private String apiKey;

    @Value("${mapbox.api.url}")          // https://api.mapbox.com/directions/v5/mapbox/driving
    private String directionsUrl;

    @Value("${mapbox.geocode.url}")      // https://api.mapbox.com/geocoding/v5/mapbox.places
    private String geocodeUrl;

    public MapboxService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*--------------------------------------------------
     * 1) Geocode a place name → lat/lon
     *--------------------------------------------------*/
    public JSONObject geocodeAddress(String address) {
        try {
            String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);

            String url = String.format(
                    "%s/%s.json?access_token=%s&limit=1&country=IN",
                    geocodeUrl, encoded, apiKey
            );

            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response);

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Failed geocoding");
        }
    }

    /*--------------------------------------------------
     * 2) Autocomplete search
     *--------------------------------------------------*/
    public JSONObject searchPlaces(String query) {
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String url = String.format(
                    "%s/%s.json?access_token=%s&autocomplete=true&limit=5&country=IN",
                    geocodeUrl, encoded, apiKey
            );

            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response);

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Failed search");
        }
    }

    /*--------------------------------------------------
     * 3) Directions API → distance + duration + geometry
     *--------------------------------------------------*/
    public JSONObject getDirections(double originLng, double originLat,
                                    double destLng, double destLat) {

        try {
            String coordinates = String.format(
                    "%f,%f;%f,%f", originLng, originLat, destLng, destLat);

            String url = String.format(
                    "%s/%s?access_token=%s&geometries=geojson&overview=full",
                    directionsUrl, coordinates, apiKey
            );

            String response = restTemplate.getForObject(url, String.class);
            return new JSONObject(response);

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Failed directions");
        }
    }

    /*--------------------------------------------------
     * 4) Extract distance + ETA
     *--------------------------------------------------*/
    public double[] getDistanceAndDuration(double originLng, double originLat,
                                           double destLng, double destLat) {
        try {
            JSONObject json = getDirections(originLng, originLat, destLng, destLat);

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
}

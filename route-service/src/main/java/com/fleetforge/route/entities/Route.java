package com.fleetforge.route.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    // 🧭 Coordinates
    private Double sourceLat;
    private Double sourceLng;
    private Double destinationLat;
    private Double destinationLng;

    // 📏 Calculated fields
    private Double distanceKm;
    private Double estimatedDurationMinutes;
}

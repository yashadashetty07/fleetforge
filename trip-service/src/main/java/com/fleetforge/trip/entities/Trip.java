    package com.fleetforge.trip.entities;

    import jakarta.persistence.*;
    import lombok.*;
    import com.fleetforge.trip.entities.TripStatus;

    @Entity
    @Table(name = "trips")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Trip {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String origin;
        private String destination;

        @Column(name = "origin_lat")
        private double originLat;

        @Column(name = "origin_lng")
        private double originLng;

        @Column(name = "destination_lat")
        private double destinationLat;

        @Column(name = "destination_lng")
        private double destinationLng;

        private Long vehicleId;
        private Long driverId;

        private double distance;
        private double eta;

        @Enumerated(EnumType.STRING)
        private TripStatus status = TripStatus.PENDING;

    }

package com.fleetforge.vehicle.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name ="make",nullable = false)
    private String make;

    @Column(name ="model",nullable = false)
    private String model;

    @Column(name ="year",nullable = false)
    private Integer year;

    @Column(name ="capacity",nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name ="status",nullable = false)
    private Status status = Status.AVAILABLE; // default available

    public enum Status {
        AVAILABLE,
        ON_TRIP,
        MAINTENANCE
    }
}


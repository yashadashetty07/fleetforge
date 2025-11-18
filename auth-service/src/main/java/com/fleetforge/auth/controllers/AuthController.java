package com.fleetforge.auth.controllers;

import com.fleetforge.auth.dto.LoginRequest;
import com.fleetforge.auth.dto.LoginResponse;
import com.fleetforge.auth.entities.Users;
import com.fleetforge.auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("register")
    public ResponseEntity<Users> registerUser(@RequestBody Users user) {
        Users savedUser = authService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approveDriver(@PathVariable("id") Long id) {
        String message = authService.approveDriver(id);
        return ResponseEntity.ok(message);
    }


}

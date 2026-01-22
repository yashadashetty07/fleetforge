package com.fleetforge.auth.controllers;

import com.fleetforge.auth.dto.LoginRequest;
import com.fleetforge.auth.dto.LoginResponse;
import com.fleetforge.auth.entities.Users;
import com.fleetforge.auth.services.AuthService;
import com.fleetforge.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private JwtUtil jwtUtil;

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

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body,
                                            @RequestHeader("Authorization") String authHeader) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "oldPassword and newPassword required"));
        }

        try {
            String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
            if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No token"));

            String username = jwtUtil.extractUsername(token); // you might need to inject JwtUtil here

            authService.changePassword(username, oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }

    }
    @PostMapping("/create-driver-user")
    public ResponseEntity<?> createDriverUser(
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        String internalKey = request.getHeader("X-Internal-Key");

        if (!"fleetforge-2024-internal-secret".equals(internalKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Unauthorized service"));
        }

        String username = body.get("username");
        String tempPass = authService.createDriverUser(username);

        return ResponseEntity.ok(Map.of("tempPassword", tempPass));
    }


}

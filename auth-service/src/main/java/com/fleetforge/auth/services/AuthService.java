package com.fleetforge.auth.services;

import com.fleetforge.auth.dto.LoginRequest;
import com.fleetforge.auth.dto.LoginResponse;
import com.fleetforge.auth.entities.Users;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    Users registerUser(Users user);
    LoginResponse login(LoginRequest loginRequest);
    String approveDriver(Long id);
}

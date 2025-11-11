package com.fleetforge.auth.services.impl;

import com.fleetforge.auth.dto.LoginRequest;
import com.fleetforge.auth.dto.LoginResponse;
import com.fleetforge.auth.entities.Role;
import com.fleetforge.auth.entities.Users;
import com.fleetforge.auth.repositories.UsersRepository;
import com.fleetforge.auth.services.AuthService;
import com.fleetforge.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Users registerUser(Users user) {

        String encodedpassword = (passwordEncoder.encode(user.getPassword()));
        user.setPassword(encodedpassword);
        if (user.getRole() == Role.ADMIN) {
            user.setIsApproved(true);
        } else {
            user.setIsApproved(false);
        }
        return usersRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Users user = usersRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        if (!user.getIsApproved()) {
            throw new RuntimeException("User not approved yet");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getRole().name(), user.getUsername());
    }

    public String approveDriver(String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("Only drivers can be approved");
        }

        user.setIsApproved(true);
        usersRepository.save(user);

        return "Driver " + username + " approved successfully";
    }


}

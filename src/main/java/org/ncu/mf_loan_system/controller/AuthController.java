package org.ncu.mf_loan_system.controller;

import org.ncu.mf_loan_system.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String username,
            @RequestParam String password) {
        return ResponseEntity.ok(authService.authenticateUser(username, password));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestParam String username,
            @RequestParam String password) {
        try {
            return ResponseEntity.ok(authService.registerUser(username, password));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
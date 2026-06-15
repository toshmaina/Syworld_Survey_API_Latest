package com.skyworld.survey.controller;

import com.skyworld.survey.dto.AuthResponse;
import com.skyworld.survey.dto.LoginRequest;
import com.skyworld.survey.dto.RegisterRequest;
import com.skyworld.survey.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_XML_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Register a new user. Role defaults to USER unless "ADMIN" is specified.
     *
     * Request body (JSON or XML):
     * {
     *   "username": "johndoe",
     *   "email": "johndoe@gmail.com",
     *   "password": "Password@123",
     *   "role": "USER"
     * }
     */
    @PostMapping(value = "/register", consumes = {
            MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    /**
     * POST /api/auth/login
     * Authenticate and receive a JWT token.
     */
    @PostMapping(value = "/login", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_XML)
                    .body("""
                          <?xml version="1.0" encoding="UTF-8"?>
                          <error>
                            <code>401</code>
                            <message>Invalid email or password</message>
                          </error>
                          """);
        }
    }
}

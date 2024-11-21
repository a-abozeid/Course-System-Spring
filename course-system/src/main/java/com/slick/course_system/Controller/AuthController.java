package com.slick.course_system.Controller;

import com.slick.course_system.Dto.LoginRequestDto;
import com.slick.course_system.Security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = jwtUtil.generateToken(loginRequestDto.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(loginRequestDto.getEmail());
            logger.info("Generated Token: {}", token);
            logger.info("Generated RefreshToken: {}", refreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("RefreshAuth", "Refresh " + refreshToken)
                    .body("Tokens generated successfully");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username (email) or password!");
    }
}

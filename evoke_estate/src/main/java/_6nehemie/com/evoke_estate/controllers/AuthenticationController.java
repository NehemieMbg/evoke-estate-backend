package _6nehemie.com.evoke_estate.controllers;

import _6nehemie.com.evoke_estate.dto.requests.LoginDto;
import _6nehemie.com.evoke_estate.dto.requests.RegisterDto;
import _6nehemie.com.evoke_estate.dto.responses.AuthenticationResponse;
import _6nehemie.com.evoke_estate.services.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterDto request, HttpServletResponse response) {

        return ResponseEntity.ok(authenticationService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginDto request) {

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}

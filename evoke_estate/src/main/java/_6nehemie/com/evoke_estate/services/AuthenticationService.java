package _6nehemie.com.evoke_estate.services;

import _6nehemie.com.evoke_estate.dto.requests.LoginDto;
import _6nehemie.com.evoke_estate.dto.requests.RegisterDto;
import _6nehemie.com.evoke_estate.dto.responses.AuthenticationResponse;
import _6nehemie.com.evoke_estate.models.User;
import _6nehemie.com.evoke_estate.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    public AuthenticationResponse register(RegisterDto request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setLocation(request.location());
        user.setPassword(passwordEncoder.encode(request.password()));
        
        user.setRole(request.role());
        
        user = userRepository.save(user);
        
        String token = jwtService.generateToken(user);
        
        return new AuthenticationResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getLocation(),
                user.getTitle(),
                user.getDescription(),
                token
        );
    }

    public AuthenticationResponse authenticate(LoginDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userRepository.findByUsernameOrEmail(request.username()).orElseThrow();
        
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getLocation(),
                user.getTitle(),
                user.getDescription(),
                token
        );
    }
}


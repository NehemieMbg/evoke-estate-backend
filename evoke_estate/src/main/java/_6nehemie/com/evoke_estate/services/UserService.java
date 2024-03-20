package _6nehemie.com.evoke_estate.services;

import _6nehemie.com.evoke_estate.dto.user.UpdateUserEmailDto;
import _6nehemie.com.evoke_estate.dto.user.UpdateUserInfoDto;
import _6nehemie.com.evoke_estate.dto.responses.UserByUsernameResponseDto;
import _6nehemie.com.evoke_estate.dto.responses.UserResponseDto;
import _6nehemie.com.evoke_estate.dto.user.UpdateUserInfoResponseDto;
import _6nehemie.com.evoke_estate.dto.user.UpdateUserPasswordDto;
import _6nehemie.com.evoke_estate.exceptions.BadRequestException;
import _6nehemie.com.evoke_estate.exceptions.NotFoundException;
import _6nehemie.com.evoke_estate.models.User;
import _6nehemie.com.evoke_estate.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public UserResponseDto getCurrentUser(String username) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username);
        
        return user.map(value -> new UserResponseDto(
                value.getId(),
                value.getFullName(),
                value.getUsername(),
                value.getEmail(),
                value.getLocation(),
                value.getTitle(),
                value.getDescription()
        )).orElse(null);
    }
    
    public UserByUsernameResponseDto getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username);
        
        return user.map(value -> new UserByUsernameResponseDto(
                value.getId(),
                value.getFullName(),
                value.getUsername(),
                value.getTitle(),
                value.getDescription()
        )).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UpdateUserInfoResponseDto updateCurrentUser(String username, UpdateUserInfoDto request) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username);
        
        if (user.isPresent()) {
            User currentUser = user.get();
            currentUser.setFullName(request.fullName());
            currentUser.setLocation(request.location());
            currentUser.setTitle(request.title());
            currentUser.setDescription(request.description());
            
            userRepository.save(currentUser);
            
            return new UpdateUserInfoResponseDto("User info were updated", HttpStatus.CREATED.value());
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UpdateUserInfoResponseDto updateEmail(String username, UpdateUserEmailDto request) {
        
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }
        
        if (!request.email().matches(request.confirmEmail())) {
            throw new BadRequestException("Emails do not match");
        }
        
        Optional<User> user = userRepository.findByUsernameOrEmail(username);
        
        if (user.isPresent()) {
            User currentUser = user.get();
            currentUser.setEmail(request.email());
            
            userRepository.save(currentUser);
            
            return new UpdateUserInfoResponseDto("User email was updated", HttpStatus.CREATED.value());
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UpdateUserInfoResponseDto updatePassword(String username, UpdateUserPasswordDto request) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);
        
        if (optionalUser.isEmpty()) throw new NotFoundException("User not found");
        
        User user = optionalUser.get();

        System.out.println("Password Encoder: " + passwordEncoder.matches(request.password(), user.getPassword()));
        
        //! temporary: must create a custom validator afterward
        if (request.newPassword().length() <= 5) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }
        
        // Check if current password matches the one in the database
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Check if new password matches the password confirmation
        if (!request.newPassword().matches(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        
        // Check if new password is different from the current password
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }
        
        // Save the new password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return new UpdateUserInfoResponseDto("User password was updated", HttpStatus.CREATED.value());
    }
}

package _6nehemie.com.evoke_estate.controllers;

import _6nehemie.com.evoke_estate.dto.user.UpdateUserEmailDto;
import _6nehemie.com.evoke_estate.dto.user.UpdateUserInfoDto;
import _6nehemie.com.evoke_estate.dto.responses.UserByUsernameResponseDto;
import _6nehemie.com.evoke_estate.dto.responses.UserResponseDto;
import _6nehemie.com.evoke_estate.dto.user.UpdateUserInfoResponseDto;
import _6nehemie.com.evoke_estate.dto.user.UpdateUserPasswordDto;
import _6nehemie.com.evoke_estate.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Map the current user
    @GetMapping("/me")
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        return userService.getCurrentUser(username);
    }

    @PutMapping("/me")
    public UpdateUserInfoResponseDto updateCurrentUser(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdateUserInfoDto request) {

        String username = userDetails.getUsername();
        return userService.updateCurrentUser(username, request);
    }

    @PutMapping("/me/updateEmail")
    public UpdateUserInfoResponseDto updateEmail(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdateUserEmailDto request) {

        String username = userDetails.getUsername();
        return userService.updateEmail(username, request);
    }

    @PutMapping("/me/updatePassword")
    public UpdateUserInfoResponseDto updatePassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdateUserPasswordDto request) {

        String username = userDetails.getUsername();
        return userService.updatePassword(username, request);
    }
    
    @DeleteMapping("/me")
    public UpdateUserInfoResponseDto deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        return userService.deleteCurrentUser(username);
    }

    // Map user by username
    @GetMapping("/{username}")
    public UserByUsernameResponseDto getUserByUsername(@PathVariable String username) {

        return userService.getUserByUsername(username);
    }
}

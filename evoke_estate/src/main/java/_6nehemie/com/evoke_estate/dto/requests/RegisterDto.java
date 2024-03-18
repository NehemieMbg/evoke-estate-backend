package _6nehemie.com.evoke_estate.dto.requests;

import _6nehemie.com.evoke_estate.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;

public record RegisterDto(
        @NotEmpty(message = "First name is required!")
        String firstName,
        @NotEmpty(message = "Last name is required!")
        String lastName,
        @NotEmpty(message = "Username is required!")
        String username,
        @NotEmpty(message = "Email is required!")
        String email,
        @NotEmpty(message = "Location is required!")
        String location,
        @NotEmpty(message = "Password is required!")
        String password,
        @NotEmpty(message = "Password confirmation is required!")
        String passwordConfirmation,
        @Enumerated(EnumType.STRING)
        Role role
) {
}

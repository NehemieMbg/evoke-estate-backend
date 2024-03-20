package _6nehemie.com.evoke_estate.dto.requests;

import _6nehemie.com.evoke_estate.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegisterDto(
        @NotEmpty(message = "First name is required!")
        String firstName,
        @NotEmpty(message = "Last name is required!")
        String lastName,
        @NotEmpty(message = "Username is required!")
        String username,
        @Email(message = "Email is invalid!")
        @NotEmpty(message = "Email is required!")
        String email,
        @NotEmpty(message = "Password is required!")
        String password,
        @NotEmpty(message = "Password confirmation is required!")
        String passwordConfirmation,
        @NotNull(message = "Terms and conditions must be accepted!")
        Boolean termsAndConditions,
        @Enumerated(EnumType.STRING)
        Role role
) {
}

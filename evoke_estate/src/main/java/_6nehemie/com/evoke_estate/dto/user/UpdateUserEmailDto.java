package _6nehemie.com.evoke_estate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UpdateUserEmailDto(
        @Email(message = "New email is invalid")
        @NotEmpty(message = "New email is required")
        String email,
        @Email(message = "Confirm email is invalid")
        @NotEmpty(message = "Confirm email is required")
        String confirmEmail
) {
}

package _6nehemie.com.evoke_estate.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record UpdateUserPasswordDto(
        @NotEmpty(message = "Password is required")
        String password,
        @NotEmpty(message = "New password is required")
        String newPassword,
        @NotEmpty(message = "Confirm password is required")
        String confirmPassword
) {
}

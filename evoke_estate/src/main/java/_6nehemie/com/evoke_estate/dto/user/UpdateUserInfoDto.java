package _6nehemie.com.evoke_estate.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record UpdateUserInfoDto(
        @NotEmpty(message = "Username is required")
        String username,
        @NotEmpty(message = "Full name is required")
        String fullName,
        String location,
        String title,
        String description
) {
}

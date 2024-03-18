package _6nehemie.com.evoke_estate.dto.requests;

import jakarta.validation.constraints.NotEmpty;

public record LoginDto(
        @NotEmpty(message = "Username & Password are required!")
        String username,
        @NotEmpty(message = "Username & Password are required!")
        String password) {
}

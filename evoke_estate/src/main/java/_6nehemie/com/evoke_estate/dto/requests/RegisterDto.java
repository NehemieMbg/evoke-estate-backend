package _6nehemie.com.evoke_estate.dto.requests;

import _6nehemie.com.evoke_estate.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record RegisterDto(
        String firstName,
        String lastName,
        String username,
        String email,
        String location,
        String password,
        String passwordConfirmation,
        @Enumerated(EnumType.STRING)
        Role role
) {
}

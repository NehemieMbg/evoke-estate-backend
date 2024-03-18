package _6nehemie.com.evoke_estate.dto.responses;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email,
        String country,
        String title,
        String description
) {
}

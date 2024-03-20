package _6nehemie.com.evoke_estate.dto.responses;

public record UserByUsernameResponseDto(
        Long id,
        String fullName,
        String username,
        String title,
        String description
) {
}

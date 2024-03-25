package _6nehemie.com.evoke_estate.dto.responses;

public record UserResponseDto(
        Long id,
        String fullName,
        String username,
        String email,
        String avatar,
        String location,
        String title,
        String description
) {
}

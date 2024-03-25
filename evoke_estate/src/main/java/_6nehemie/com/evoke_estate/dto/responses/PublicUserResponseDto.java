package _6nehemie.com.evoke_estate.dto.responses;

public record PublicUserResponseDto(
        Long id,
        String fullName,
        String username,
        String avatar,
        String location,
        String title,
        String description
) {
}

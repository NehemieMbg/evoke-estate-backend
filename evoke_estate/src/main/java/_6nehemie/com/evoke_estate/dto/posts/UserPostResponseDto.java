package _6nehemie.com.evoke_estate.dto.posts;

public record UserPostResponseDto(
        Long id,
        String fullName,
        String username,
        String avatar,
        String location,
        String title,
        String description
) {
}

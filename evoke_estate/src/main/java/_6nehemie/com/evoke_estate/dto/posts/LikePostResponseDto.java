package _6nehemie.com.evoke_estate.dto.posts;

public record LikePostResponseDto(
        Boolean isLiked,
        String message,
        Integer status
) {
}

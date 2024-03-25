package _6nehemie.com.evoke_estate.dto.posts;

import java.util.List;

public record TotalLikesPostResponseDto(
        Long postId,
        List<UserPostResponseDto> users,
        Integer totalLikes
) {
}

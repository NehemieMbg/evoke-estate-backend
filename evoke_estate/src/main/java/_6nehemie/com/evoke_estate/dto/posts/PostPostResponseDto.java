package _6nehemie.com.evoke_estate.dto.posts;


import _6nehemie.com.evoke_estate.dto.responses.PublicUserResponseDto;

import java.util.List;
import java.util.Set;

public record PostPostResponseDto(
        Long id,
        String title,
        UserPostResponseDto author,
        String image,
        String description,
        Long views,
        List<PublicUserResponseDto> likedBy
) {
}

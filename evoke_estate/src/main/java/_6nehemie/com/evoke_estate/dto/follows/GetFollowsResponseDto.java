package _6nehemie.com.evoke_estate.dto.follows;

import _6nehemie.com.evoke_estate.dto.responses.UserByUsernameResponseDto;

import java.util.List;

public record GetFollowsResponseDto(
        List<UserByUsernameResponseDto> followers
//        List<UserByUsernameResponseDto> following
        
) {
}

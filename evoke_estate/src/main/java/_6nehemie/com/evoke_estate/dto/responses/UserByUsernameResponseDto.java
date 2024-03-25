package _6nehemie.com.evoke_estate.dto.responses;

import _6nehemie.com.evoke_estate.dto.user.UserUsernameSimpleResponseDto;

import java.util.List;

public record UserByUsernameResponseDto(
        Long id,
        String fullName,
        String username,
        String avatar,
        String title,
        String description,
        List<UserUsernameSimpleResponseDto> followers,
        List<UserUsernameSimpleResponseDto> following

) {
}

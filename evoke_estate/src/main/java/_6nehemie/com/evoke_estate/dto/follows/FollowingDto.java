package _6nehemie.com.evoke_estate.dto.follows;

import jakarta.validation.constraints.NotEmpty;

public record FollowingDto(
        @NotEmpty
        String following
) {
}

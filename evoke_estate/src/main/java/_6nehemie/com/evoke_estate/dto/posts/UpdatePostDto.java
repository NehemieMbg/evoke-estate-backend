package _6nehemie.com.evoke_estate.dto.posts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdatePostDto
        (
                @NotNull
                Long id,
                @NotEmpty
                String title,
                String description
        ) {
}

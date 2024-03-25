package _6nehemie.com.evoke_estate.dto.posts;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

public record PostDto(
        @NotEmpty(message = "title is required")
        String title,
        String description,
        MultipartFile image
) {
}

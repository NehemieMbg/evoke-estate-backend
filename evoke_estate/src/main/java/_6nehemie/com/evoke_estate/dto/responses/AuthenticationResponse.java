package _6nehemie.com.evoke_estate.dto.responses;

public record AuthenticationResponse(
        Long id,
        String fullName,
        String username,
        String email,
        String location,
        String title,
        String description,
        String token
) {

}

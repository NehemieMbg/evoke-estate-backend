package _6nehemie.com.evoke_estate.controllers;

import _6nehemie.com.evoke_estate.dto.follows.FollowingResponseDto;
import _6nehemie.com.evoke_estate.services.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class FollowController {
    
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow/{username}")
    public ResponseEntity<FollowingResponseDto> followUser(@AuthenticationPrincipal UserDetails userDetails, @Validated @PathVariable(name = "username") String usernameFollowing) {
        
        return ResponseEntity.ok(followService.followUser(userDetails.getUsername(), usernameFollowing));
    }
    
    @DeleteMapping("/unfollow/{username}")
    public ResponseEntity<FollowingResponseDto> unfollowUser(@AuthenticationPrincipal UserDetails userDetails, @Validated @PathVariable(name = "username") String usernameFollowing) {
        
        return ResponseEntity.ok(followService.unfollowUser(userDetails.getUsername(), usernameFollowing));
    }
}

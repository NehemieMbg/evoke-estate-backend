package _6nehemie.com.evoke_estate.services;

import _6nehemie.com.evoke_estate.dto.follows.FollowingResponseDto;
import _6nehemie.com.evoke_estate.exceptions.NotFoundException;
import _6nehemie.com.evoke_estate.models.Follow;
import _6nehemie.com.evoke_estate.models.User;
import _6nehemie.com.evoke_estate.repositories.FollowRepository;
import _6nehemie.com.evoke_estate.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public FollowService(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    public FollowingResponseDto followUser(String username, String usernameFollowing) {

        User follower = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        User following = userRepository.findByUsernameOrEmail(usernameFollowing)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        if (follower.equals(following)) {
            return new FollowingResponseDto("You cannot follow yourself", 400);
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        
        followRepository.save(follow);
        
        return new FollowingResponseDto("User followed successfully", 200);
    }

    public FollowingResponseDto unfollowUser(String username, String usernameFollowing) {
            
            User follower = userRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            
            User following = userRepository.findByUsernameOrEmail(usernameFollowing)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            
            if (follower.equals(following)) {
                return new FollowingResponseDto("You cannot unfollow yourself", 400);
            }
    
            Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            
            followRepository.delete(follow);
            
            return new FollowingResponseDto("User unfollowed successfully", 200);
    }
}

package _6nehemie.com.evoke_estate.repositories;

import _6nehemie.com.evoke_estate.models.Follow;
import _6nehemie.com.evoke_estate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<User> findAllByFollower(User user);
    
    Boolean existsByFollowerAndFollowing(User follower, User following);
    
}

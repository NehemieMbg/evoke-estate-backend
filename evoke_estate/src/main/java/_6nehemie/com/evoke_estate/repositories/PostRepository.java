package _6nehemie.com.evoke_estate.repositories;

import _6nehemie.com.evoke_estate.models.Follow;
import _6nehemie.com.evoke_estate.models.Post;
import _6nehemie.com.evoke_estate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findAllByUser(User user);
    void deleteAllByUser(User user);
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByUserOrderByCreatedAtDesc(User user);

    List<Post> findAllByUserInOrderByCreatedAtDesc(Set<User> following);
}

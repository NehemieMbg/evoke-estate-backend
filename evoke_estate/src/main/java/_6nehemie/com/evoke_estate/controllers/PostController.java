package _6nehemie.com.evoke_estate.controllers;

import _6nehemie.com.evoke_estate.dto.posts.*;
import _6nehemie.com.evoke_estate.services.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostPostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostPostResponseDto>> getPostsByUsername(
            @NotEmpty(message = "username is required")
            @PathVariable String username
    ) {

        return ResponseEntity.ok(postService.getPostsByUsername(username));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostPostResponseDto> getPostById(@Validated @PathVariable Long postId) {

        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @PostMapping
    public ResponseEntity<PostPostResponseDto> createPost(@AuthenticationPrincipal UserDetails userDetails, @Valid @ModelAttribute PostDto request) {

        return ResponseEntity.ok(postService.createPost(userDetails.getUsername(), request));
    }

    @PutMapping
    public ResponseEntity<PostPostResponseDto> updatePost(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdatePostDto body) {

        return ResponseEntity.ok(postService.updatePost(userDetails.getUsername(), body));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<DeletePostResponseDto> deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Validated
            @PathVariable Long postId
    ) {

        return ResponseEntity.ok(postService.deletePost(userDetails.getUsername(), postId));
    }

    // ? Like a post

    // get the users that liked a post
    @GetMapping("/{postId}/likes")
    public ResponseEntity<TotalLikesPostResponseDto> getLikes(@PathVariable Long postId) {
        
        return ResponseEntity.ok(postService.getLikes(postId));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<LikePostResponseDto> likePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.likePost(userDetails.getUsername(), postId));
    }
}

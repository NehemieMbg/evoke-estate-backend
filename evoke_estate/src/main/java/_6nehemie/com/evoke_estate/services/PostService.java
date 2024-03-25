package _6nehemie.com.evoke_estate.services;

import _6nehemie.com.evoke_estate.dto.S3UploadDto;
import _6nehemie.com.evoke_estate.dto.posts.*;
import _6nehemie.com.evoke_estate.dto.responses.PublicUserResponseDto;
import _6nehemie.com.evoke_estate.dto.responses.UserResponseDto;
import _6nehemie.com.evoke_estate.exceptions.BadRequestException;
import _6nehemie.com.evoke_estate.exceptions.NotFoundException;
import _6nehemie.com.evoke_estate.models.Follow;
import _6nehemie.com.evoke_estate.models.Post;
import _6nehemie.com.evoke_estate.models.User;
import _6nehemie.com.evoke_estate.repositories.PostRepository;
import _6nehemie.com.evoke_estate.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, S3Service s3Service, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.s3Service = s3Service;
        this.userRepository = userRepository;
    }

    public List<PostPostResponseDto> getAllPosts() {

        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        posts.get(0).getLikedBy();

        // Convert the list of posts to a list of PostPostResponseDto
        return posts.stream().map(post -> new PostPostResponseDto(
                post.getId(),
                post.getTitle(),
                // Convert the user to a UserPostResponseDto
                toUserPostResponseDto(post),
                post.getImage(),
                post.getDescription(),
                post.getViews(),
                post.getLikedBy().stream().map(
                        user -> new PublicUserResponseDto(
                                user.getId(),
                                user.getFullName(),
                                user.getUsername(),
                                user.getAvatar(),
                                user.getLocation(),
                                user.getTitle(),
                                user.getDescription()
                        )
                ).toList()
        )).toList();
    }


    public List<PostPostResponseDto> getPostsByUsername(String username) {

        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        System.out.println("User: " + user.getUsername());

        List<Post> posts = postRepository.findAllByUserOrderByCreatedAtDesc(user);

        return posts.stream().map(post -> new PostPostResponseDto(
                post.getId(),
                post.getTitle(),
                // Convert the user to a UserPostResponseDto
                toUserPostResponseDto(post),
                post.getImage(),
                post.getDescription(),
                post.getViews(),
                post.getLikedBy().stream().map(
                        likedBy -> new PublicUserResponseDto(
                                user.getId(),
                                user.getFullName(),
                                user.getUsername(),
                                user.getAvatar(),
                                user.getLocation(),
                                user.getTitle(),
                                user.getDescription()
                        )
                ).toList()
        )).toList();
    }

    public PostPostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("Post not found")
        );

        // Increment the views
        post.setViews(post.getViews() + 1);
        postRepository.save(post);

        return new PostPostResponseDto(
                post.getId(),
                post.getTitle(),
                // Convert the user to a UserPostResponseDto
                toUserPostResponseDto(post),
                post.getImage(),
                post.getDescription(),
                post.getViews(),
                post.getLikedBy().stream().map(
                        user -> new PublicUserResponseDto(
                                user.getId(),
                                user.getFullName(),
                                user.getUsername(),
                                user.getAvatar(),
                                user.getLocation(),
                                user.getTitle(),
                                user.getDescription()
                        )
                ).toList()
        );
    }

    @Transactional
    public PostPostResponseDto createPost(String username, PostDto request) {
        // Already sure that the user exists, @AuthenticationPrincipal UserDetails in controller

        //? Checks if the file is present
        if (request.image() == null) {
            throw new BadRequestException("Image is required");
        }

        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        S3UploadDto file = s3Service.uploadFile(request.image());

        Post post = new Post();
        post.setTitle(request.title());
        post.setUser(user); // ? Set the user later
        post.setDescription(request.description());
        post.setImage(file.fileUrl());
        post.setImageKey(file.fileKey());

        postRepository.save(post);

        return new PostPostResponseDto(
                post.getId(),
                post.getTitle(),
                // Convert the user to a UserPostResponseDto
                toUserPostResponseDto(post),
                post.getImage(),
                post.getDescription(),
                post.getViews(),
                post.getLikedBy().stream().map(
                        likedBy -> new PublicUserResponseDto(
                                user.getId(),
                                user.getFullName(),
                                user.getUsername(),
                                user.getAvatar(),
                                user.getLocation(),
                                user.getTitle(),
                                user.getDescription()
                        )
                ).toList()
        );
    }

    public PostPostResponseDto updatePost(String username, UpdatePostDto body) {

        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        // Retrieve the post
        Post post = postRepository.findById(body.id()).orElseThrow(
                () -> new NotFoundException("Post not found")
        );

        // Check if authenticated user is the owner of the post
        if (!post.getUser().getUsername().equals(user.getUsername())) {
            throw new BadRequestException("You are not the owner of this post");
        }

        // Update the post
        post.setTitle(body.title());
        post.setDescription(body.description());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return new PostPostResponseDto(
                post.getId(),
                post.getTitle(),
                // Convert the user to a UserPostResponseDto
                toUserPostResponseDto(post),
                post.getImage(),
                post.getDescription(),
                post.getViews(),
                post.getLikedBy().stream().map(
                        likedBy -> new PublicUserResponseDto(
                                user.getId(),
                                user.getFullName(),
                                user.getUsername(),
                                user.getAvatar(),
                                user.getLocation(),
                                user.getTitle(),
                                user.getDescription()
                        )
                ).toList()
        );
    }

    public DeletePostResponseDto deletePost(String username, Long postId) {

        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        // Retrieve the post
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("Post not found")
        );

        // Check if authenticated user is the owner of the post
        if (!post.getUser().getUsername().equals(user.getUsername())) {
            throw new BadRequestException("You are not the owner of this post");
        }

        // Delete the image from S3
        if (post.getImageKey() != null) {
            s3Service.deleteFile(post.getImageKey());
        }

        // Delete the post
        postRepository.delete(post);

        return new DeletePostResponseDto("The post was successfully deleted", HttpStatus.NO_CONTENT.value());
    }

    private UserPostResponseDto toUserPostResponseDto(Post post) {
        return new UserPostResponseDto(
                post.getUser().getId(),
                post.getUser().getFullName(),
                post.getUser().getUsername(),
                post.getUser().getAvatar(),
                post.getUser().getLocation(),
                post.getUser().getTitle(),
                post.getUser().getDescription()
        );
    }

    public void deleteAllPostsByUser(User user) {
        List<Post> posts = postRepository.findAllByUser(user);

        for (Post post : posts) {
            if (post.getImageKey() != null) {
                s3Service.deleteFile(post.getImageKey());
            }
        }

        postRepository.deleteAllByUser(user);
    }

    public LikePostResponseDto likePost(String username, Long postId) {
        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("Post not found")
        );

        // Check if the user has already liked the post
        if (post.getLikedBy().contains(user)) {
            
            post.getLikedBy().remove(user);
            postRepository.save(post);
            return new LikePostResponseDto(false, "Post unliked successfully", HttpStatus.OK.value());
        } else {
            
            post.getLikedBy().add(user);
            postRepository.save(post);
            return new LikePostResponseDto(true, "Post liked successfully", HttpStatus.OK.value());
        }
    }

    public TotalLikesPostResponseDto getLikes(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("Post not found")
        );

        return new TotalLikesPostResponseDto(
                post.getId(),
                post.getLikedBy().stream().map(user -> new UserPostResponseDto(
                        user.getId(),
                        user.getFullName(),
                        user.getUsername(),
                        user.getAvatar(),
                        user.getLocation(),
                        user.getTitle(),
                        user.getDescription()
                )).toList(),
                post.getLikedBy().size()
        );
    }
}

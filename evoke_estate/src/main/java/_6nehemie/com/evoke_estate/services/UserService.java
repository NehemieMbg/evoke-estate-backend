package _6nehemie.com.evoke_estate.services;

import _6nehemie.com.evoke_estate.dto.S3UploadDto;
import _6nehemie.com.evoke_estate.dto.user.*;
import _6nehemie.com.evoke_estate.dto.responses.UserByUsernameResponseDto;
import _6nehemie.com.evoke_estate.dto.responses.UserResponseDto;
import _6nehemie.com.evoke_estate.exceptions.BadRequestException;
import _6nehemie.com.evoke_estate.exceptions.NotFoundException;
import _6nehemie.com.evoke_estate.models.User;
import _6nehemie.com.evoke_estate.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final S3Service s3Service;
    private final PostService postService;
    private final UserRepository userRepository;
    
    private final FollowService followService;
    private final PasswordEncoder passwordEncoder;

    public UserService(S3Service s3Service, PostService postService, UserRepository userRepository, FollowService followService, PasswordEncoder passwordEncoder) {
        this.s3Service = s3Service;
        this.postService = postService;
        this.userRepository = userRepository;
        this.followService = followService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto getCurrentUser(String username) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username);

        return user.map(value -> new UserResponseDto(
                value.getId(),
                value.getFullName(),
                value.getUsername(),
                value.getEmail(),
                value.getAvatar(),
                value.getLocation(),
                value.getTitle(),
                value.getDescription()
        )).orElse(null);
    }

    public UserByUsernameResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(
            () -> new NotFoundException("User not found")
        );
        
        List<UserUsernameSimpleResponseDto> following = user.getFollowing().stream().map(
                currentFollowing -> new UserUsernameSimpleResponseDto(
                        currentFollowing.getFollower().getId(),
                        currentFollowing.getFollower().getFullName(),
                        currentFollowing.getFollower().getUsername(),
                        currentFollowing.getFollower().getAvatar(),
                        currentFollowing.getFollower().getTitle(),
                        currentFollowing.getFollower().getDescription()
                )
        ).toList();
        
        List<UserUsernameSimpleResponseDto> followers = user.getFollowers().stream().map(
                currentFollower -> new UserUsernameSimpleResponseDto(
                        currentFollower.getFollowing().getId(),
                        currentFollower.getFollowing().getFullName(),
                        currentFollower.getFollowing().getUsername(),
                        currentFollower.getFollowing().getAvatar(),
                        currentFollower.getFollowing().getTitle(),
                        currentFollower.getFollowing().getDescription()
                )
        ).toList();
        
        return new UserByUsernameResponseDto(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getAvatar(),
                user.getTitle(),
                user.getDescription(),
                followers,
                following
        );
    }

    public UpdateUserInfoResponseDto updateCurrentUser(String username, UpdateUserInfoDto request) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username);

        if (user.isPresent()) {
            User currentUser = user.get();
            currentUser.setFullName(request.fullName());
            currentUser.setLocation(request.location());
            currentUser.setTitle(request.title());
            currentUser.setDescription(request.description());

            userRepository.save(currentUser);

            return new UpdateUserInfoResponseDto("User info were updated", HttpStatus.CREATED.value());
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UpdateUserInfoResponseDto updateEmail(String username, UpdateUserEmailDto request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        if (!request.email().matches(request.confirmEmail())) {
            throw new BadRequestException("Emails do not match");
        }

        Optional<User> user = userRepository.findByUsernameOrEmail(username);

        if (user.isPresent()) {
            User currentUser = user.get();
            currentUser.setEmail(request.email());

            userRepository.save(currentUser);

            return new UpdateUserInfoResponseDto("User email was updated", HttpStatus.CREATED.value());
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public UpdateUserInfoResponseDto updatePassword(String username, UpdateUserPasswordDto request) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);

        if (optionalUser.isEmpty()) throw new NotFoundException("User not found");

        User user = optionalUser.get();

        System.out.println("Password Encoder: " + passwordEncoder.matches(request.password(), user.getPassword()));

        //! temporary: must create a custom validator afterward
        if (request.newPassword().length() <= 5) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        // Check if current password matches the one in the database
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Check if new password matches the password confirmation
        if (!request.newPassword().matches(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // Check if new password is different from the current password
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }

        // Save the new password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return new UpdateUserInfoResponseDto("User password was updated", HttpStatus.CREATED.value());
    }

    @Transactional
    public UpdateUserInfoResponseDto deleteCurrentUser(String username) {
        
        User user = userRepository.findByUsernameOrEmail(username).orElseThrow(
            () -> new NotFoundException("User not found")
        );
        
        //? Check if user has an avatar and delete it from S3
        if (user.getAvatarKey() != null) {
            s3Service.deleteFile(user.getAvatarKey());
        }
        
        if (!userRepository.existsByUsername(username)) {
            throw new NotFoundException("User not found");
        }
        
        postService.deleteAllPostsByUser(user);

        userRepository.deleteByUsername(username);
        
        return new UpdateUserInfoResponseDto("User was deleted", HttpStatus.OK.value());
    }
    
    public AvatarUploadResponseDto updateAvatar(String username, MultipartFile avatar) {

        System.out.println("File: " + avatar);
        
        // Find user by username
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);
        User user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));
        
        // Remove previous avatar from S3

        //? Check if user has an avatar and delete it from S3
        if (user.getAvatar() != null && user.getAvatarKey() != null) {
            s3Service.deleteFile(user.getAvatarKey());
        }
        
        // Upload new avatar to S3
        S3UploadDto avatarUrl = s3Service.uploadFile(avatar);

        //? Update the new user avatar URL and KEY in the database
        user.setAvatar(avatarUrl.fileUrl());
        user.setAvatarKey(avatarUrl.fileKey());
        userRepository.save(user);
        
        return new AvatarUploadResponseDto(user.getAvatar(), "Avatar was updated", HttpStatus.CREATED.value());
    }

    public AvatarDeleteResponseDto deleteAvatar(String username) {
            
            // Find user by username
            Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username);
            User user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));
            
            //? Check if user has an avatar and delete it from S3
            if (user.getAvatarKey() != null) {
                s3Service.deleteFile(user.getAvatarKey());
            }
            
            //? Update the new user avatar URL and KEY in the database
            user.setAvatar(null);
            user.setAvatarKey(null);
            userRepository.save(user);
            
            return new AvatarDeleteResponseDto("Avatar was deleted", HttpStatus.OK.value());
    }
}

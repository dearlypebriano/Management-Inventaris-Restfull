package com.management.ManagementInventaris.user;

import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.UserDetailToken;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserDetailToken userDetailToken;

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new IllegalStateException("Wrong password");
        if (!request.getNewPassword().equals(request.getConfirmationPassword()))
            throw new IllegalStateException("Password are not the same");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void followUser(String userIdToFollow) {
        User currentUser = userDetailToken.dataUserEmail();
        User userToFollow = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new IllegalArgumentException("User to follow does not exist"));

        if (currentUser.getId().equals(userToFollow.getId()))
            throw new IllegalArgumentException("Cannot follow yourself");

        if (currentUser.getFollowing().contains(userToFollow)) {
            currentUser.getFollowing().remove(userToFollow);
            userToFollow.getFollowers().remove(currentUser);

            currentUser.setFollowingCount(currentUser.getFollowingCount() - 1);
            userToFollow.setFollowersCount(userToFollow.getFollowersCount() - 1);
        } else {
            currentUser.getFollowing().add(userToFollow);
            userToFollow.getFollowers().add(currentUser);

            currentUser.setFollowingCount(currentUser.getFollowingCount() + 1);
            userToFollow.setFollowersCount(userToFollow.getFollowersCount() + 1);
        }

        userRepository.save(currentUser);
        userRepository.save(userToFollow);
    }

    @Override
    @Transactional
    public void unfollowUser(String userId) {
        User currentUser = userDetailToken.dataUserEmail();
        User userToUnfollow = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Could not find user to unfollow"));

        if (currentUser.getId().equals(userToUnfollow.getId()))
            throw new IllegalArgumentException("Cannot unfollow yourself");

        if (currentUser.getFollowing().contains(userToUnfollow)) {
            currentUser.getFollowing().remove(userToUnfollow);
            userToUnfollow.getFollowers().remove(currentUser);

            currentUser.setFollowingCount(currentUser.getFollowingCount() - 1);
            userToUnfollow.setFollowersCount(userToUnfollow.getFollowersCount() - 1);
        } else {
            throw new IllegalStateException("User is not followed");
        }

        userRepository.save(currentUser);
        userRepository.save(userToUnfollow);
    }

    @Override
    @Transactional
    public void like(String userIdToLike) {
        User currentUser = userDetailToken.dataUserEmail();
        User userToLike = userRepository.findById(userIdToLike)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if (currentUser.equals(userToLike)) throw new IllegalArgumentException("Cannot like yourself");
        if (currentUser.getLikes().contains(userToLike)) throw new IllegalStateException("User already liked");

        currentUser.getLikes().add(userToLike);
        userToLike.getLikedUsers().add(currentUser);

        currentUser.setLikesCount(currentUser.getLikesCount() + 1);
        userToLike.setLikedUsersCount(userToLike.getLikedUsersCount() + 1);

        userRepository.save(currentUser);
        userRepository.save(userToLike);
    }

    @Override
    @Transactional
    public void unlike(String userIdToUnlike) {
        User currentUser = userDetailToken.dataUserEmail();
        User userToUnlike = userRepository.findById(userIdToUnlike)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if (currentUser.equals(userToUnlike)) throw new IllegalArgumentException("Cannot unlike yourself");
        if (!currentUser.getLikes().contains(userToUnlike)) throw new IllegalStateException("User not liked");

        currentUser.getLikes().remove(userToUnlike);
        userToUnlike.getLikedUsers().remove(currentUser);

        currentUser.setLikesCount(currentUser.getLikesCount() - 1);
        userToUnlike.setLikedUsersCount(userToUnlike.getLikedUsersCount() - 1);

        userRepository.save(currentUser);
        userRepository.save(userToUnlike);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'getFollowers:' + #userId")
    public WebResponse<List<UserProfile>> getFollowers(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User Does Not Exist"));

        List<UserProfile> userProfilesFollowers = user.getFollowers().stream()
                .map(this::convertToUserProfile)
                .toList();

        PagingResponse pagingResponse = PagingResponse.builder()
                .size(userProfilesFollowers.size())
                .build();

        return WebResponse.<List<UserProfile>>builder()
                .data(userProfilesFollowers)
                .paging(pagingResponse)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'getCurrentUserFollowers:' + #currentUser.getId()")
    public WebResponse<List<UserProfile>> getCurrentUserFollowers(User currentUser) {
        List<UserProfile> followers = currentUser.getFollowers().stream()
                .map(this::convertToUserProfile)
                .toList();

        PagingResponse pagingResponse = PagingResponse.builder()
                .size(followers.size())
                .build();

        return WebResponse.<List<UserProfile>>builder()
                .data(followers)
                .paging(pagingResponse)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'getFollowing:' + #userId")
    public WebResponse<List<UserProfile>> getFollowing(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User Does Not Exists"));

        List<UserProfile> following = user.getFollowing().stream()
                .map(this::convertToUserProfile)
                .toList();

        PagingResponse pagingResponse = PagingResponse.builder()
                .size(following.size())
                .build();

        return WebResponse.<List<UserProfile>>builder()
                .data(following)
                .paging(pagingResponse)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'getCurrentUserFollowing:' + #currentUser.getId()")
    public WebResponse<List<UserProfile>> getCurrentUserFollowing(User currentUser) {
        List<UserProfile> following = currentUser.getFollowing().stream()
                .map(this::convertToUserProfile)
                .toList();

        PagingResponse pagingResponse = PagingResponse.builder()
                .size(following.size())
                .build();

        return WebResponse.<List<UserProfile>>builder()
                .data(following)
                .paging(pagingResponse)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    public void deleteAccount(String userId) {
        User token = userDetailToken.dataUserEmail();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User does not exist"));

        if (!token.getEmail().equals(user.getEmail())) throw new IllegalStateException("Users cannot delete the account!");

        deleteFileFromMinIO(user);
        userRepository.delete(user);
    }
}
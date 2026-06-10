package application.service.user;

import application.entity.user.Follow;
import application.entity.user.UserEntity;
import application.repository.user.FollowRepository;
import application.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FollowServiceImplementation implements FollowService{

    private final UserRepository userRepository;

    private final FollowRepository followRepository;

    public FollowServiceImplementation(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    @Override
    public ResponseEntity<?> followUser(String userToFollower_id, UserDetails userDetails) {
        String username = userDetails.getUsername();
        UserEntity current_user = this.userRepository.findByUsername(username);
        Optional<UserEntity> optionalUser = this.userRepository.findById(UUID.fromString(userToFollower_id));
        if(current_user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        if (optionalUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        UserEntity user_to_follow = optionalUser.get();

        if(current_user.getId().equals(user_to_follow.getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user cannot follow yourself");
        if(isFollowing(String.valueOf(current_user.getId()), String.valueOf(user_to_follow.getId()))) return ResponseEntity.status(HttpStatus.CONFLICT).body("already following user");
        Follow follow = Follow.builder()
                .follower(current_user)
                .followee(user_to_follow)
                .build();

        this.followRepository.save(follow);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("you followed " + user_to_follow.getUsername());
    }

    @Override
    public ResponseEntity<?> unfollowUser(String userToFollower_id, UserDetails userDetails) {
        String username = userDetails.getUsername();
        UserEntity current_user = this.userRepository.findByUsername(username);
        Optional<UserEntity> optionalUser = this.userRepository.findById(UUID.fromString(userToFollower_id));
        if(current_user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        if (optionalUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        UserEntity user_to_follow = optionalUser.get();
        if(!isFollowing(String.valueOf(current_user.getId()), String.valueOf(user_to_follow.getId()))) return ResponseEntity.status(HttpStatus.CONFLICT).body("you are not following this user");
        followRepository.deleteByFollowerAndFollowing(user_to_follow.getId(), current_user.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("you unfollow this user");
    }

    @Override
    public ResponseEntity<?> getAllCurrentUserFollowers(UserDetails userDetails) {

        String username = userDetails.getUsername();

        UserEntity current_user = this.userRepository.findByUsername(username);

        List<Follow> followers = this.followRepository.findByFollower(current_user);

        if (followers.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("you don't have any followers");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(followers);
    }

    @Override
    public ResponseEntity<?> getAllCurrentUserFollowees(UserDetails userDetails) {
        return null;
    }

    @Override
    public boolean isFollowing(String currentUserId, String userIdToFollow) {
        return this.followRepository.existsByFollowerAndFollowee(currentUserId, userIdToFollow);
    }
}

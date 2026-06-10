package application.service.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface FollowService {

    ResponseEntity<?> followUser(String follower_id, UserDetails userDetails);

    ResponseEntity<?> unfollowUser(String follower_id, UserDetails userDetails);

    ResponseEntity<?> getAllCurrentUserFollowers(UserDetails userDetails);

    ResponseEntity<?> getAllCurrentUserFollowees(UserDetails userDetails);

    public boolean isFollowing(String currentUserId, String userIdToFollow);

}

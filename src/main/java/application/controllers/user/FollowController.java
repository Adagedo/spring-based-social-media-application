package application.controllers.user;

import application.service.user.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/{user_to_follow_id}")
    public ResponseEntity<?> follow(
            @PathVariable String user_to_follow_id,
            @AuthenticationPrincipal UserDetails userDetails
            ){

        return this.followService.followUser(user_to_follow_id, userDetails);
    }

    @DeleteMapping("/{user_to_unFollow}")
    public ResponseEntity<?> unfollow(
            @PathVariable String user_to_unFollow,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return this.followService.unfollowUser(user_to_unFollow, userDetails);
    }
}

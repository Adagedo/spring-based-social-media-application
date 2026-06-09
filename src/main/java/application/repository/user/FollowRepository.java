package application.repository.user;

import application.entity.user.Follow;
import application.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    List<Follow> findByFollower(UserEntity follower);

    List<Follow> findByFollowee(UserEntity followee);

    boolean existsByFollowerAndFollowee(UserEntity follower, UserEntity followee);

    void deleteByFollowerAndFollowing(UUID followerId, UUID followingId);

}

package application.repository.post;

import application.entity.post.PostEntity;
import application.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    PostEntity findByTitle(String title);
    List<PostEntity> findByUser(UserEntity user);
}

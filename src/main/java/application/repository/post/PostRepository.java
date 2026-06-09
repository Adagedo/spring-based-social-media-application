package application.repository.post;

import application.entity.post.PostEntity;
import application.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    PostEntity findByTitle(String title);
    List<PostEntity> findByUser(UserEntity user);
}

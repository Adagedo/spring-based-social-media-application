package application.repository.post;

import application.entity.post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    PostEntity findByTitle(String title);
}

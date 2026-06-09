package application.repository.comment;

import application.entity.comment.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.replies r " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH r.user " +
            "WHERE c.id = :commentId")
    Optional<CommentEntity> findCommentsWithRepliesTree(@Param("parent_comment_id") String parent_comment_id);
}

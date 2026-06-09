package application.entity.comment;

import application.entity.BaseEntity;
import application.entity.post.PostEntity;
import application.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(
        name = "comment",
        indexes = {
                @Index(name = "idx_comment_id", columnList = "id", unique = true),
                @Index(name = "idx_post_id", columnList = "post_id", unique = true),
                @Index(name = "idx_parent_id", columnList = "parent_id", unique = true),

        }
)
public class CommentEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<CommentEntity> replies = new ArrayList<>();

    @Column
    private String image_path;

    public void addReply(CommentEntity reply){
        replies.add(reply);
        reply.setParent(this);
        reply.setPost(this.post);
    }
}

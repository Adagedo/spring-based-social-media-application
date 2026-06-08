package application.entity.post;

import application.entity.BaseEntity;
import application.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(
        name = "post",
        indexes = {
                @Index(name = "idx_post_title", columnList = "title"),
                @Index(name = "idx_post_id", columnList = "id", unique = true)
        }
)
public class PostEntity extends BaseEntity {

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

    @Column
    private String image_path;
}

package application.entity.user;

import application.entity.BaseEntity;
import application.entity.code.VerificationCodeEntity;
import application.entity.post.PostEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(
        name = "user",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true),
                @Index(name = "idx_user_username",columnList = "username"),
                @Index(name = "idx_user_id", columnList = "id", unique = true)
        }
)
public class UserEntity extends BaseEntity implements UserDetails {

    @Column(name = "username", nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "last_login_at")
    private Timestamp lastLoginAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private VerificationCodeEntity verificationCode;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL, orphanRemoval = true
    )
    @JsonManagedReference
    private List<PostEntity> posts;

    @Column(name = "is_verified")
    private boolean is_verified;

    @Column(name = "status")
    @Builder.Default
    private String status = "inactive";

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}

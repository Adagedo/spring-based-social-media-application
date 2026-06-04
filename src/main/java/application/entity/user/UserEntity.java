package application.entity.user;

import application.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.sql.Date;
import java.sql.Timestamp;


@EqualsAndHashCode(callSuper = true)
@Data
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
public class UserEntity extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "last_login_at")
    private Timestamp lastLoginAt;

    @Column(name = "email_verification_expiry")
    private Date verificationExpiry;

    @Column(name = "status")
    @Builder.Default
    private String status = "inactive";

}

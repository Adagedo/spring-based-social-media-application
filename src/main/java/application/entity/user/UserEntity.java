package application.entity.user;

import application.entity.BaseEntity;
import application.entity.code.VerificationCodeEntity;
import jakarta.persistence.*;
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private VerificationCodeEntity verificationCode;

    @Column(name = "is_verified")
    @Builder.Default
    private boolean is_verified = false;

    @Column(name = "status")
    @Builder.Default
    private String status = "inactive";

}

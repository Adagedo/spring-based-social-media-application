package application.entity.code;


import application.entity.BaseEntity;
import application.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(
        name = "verification_code",
        indexes = {
                @Index(name = "idx_verification_code_email", columnList = "user_email", unique = true),
                @Index(name = "idx_verification_code_user",columnList = "user_id", unique = true),
        }
)
public class VerificationCodeEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "user_email", nullable = false)
    private  String user_email;

    @Column(name = "code", nullable = false)
    private  String code;

    @Column(name = "code_type", nullable = false)
    private  String type;
}

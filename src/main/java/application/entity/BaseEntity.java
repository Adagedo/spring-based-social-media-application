package application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;



@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BaseEntity {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)                 // CRITICAL: Forces Hibernate 6 to use VARCHAR(36)
    @Column(name = "id", length = 225, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private Timestamp updateAt;
}

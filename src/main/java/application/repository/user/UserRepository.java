package application.repository.user;

import application.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByUsername(String username);
    Optional<UserEntity> findUserByEmail(String email);
}

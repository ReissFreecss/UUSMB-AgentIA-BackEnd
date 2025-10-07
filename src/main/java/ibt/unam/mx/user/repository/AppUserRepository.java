package ibt.unam.mx.user.repository;

import ibt.unam.mx.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findFirstByEmailAndRecoveryCode(String email, String recoveryCode);
    Optional<AppUser> findFirstByRecoveryCode(String recoveryCode);
    boolean existsByEmailAndIdNot(String email, Long id);
    List<AppUser> findByStatus(boolean status);

    @Query("SELECT u FROM AppUser u ORDER BY u.id")
    List<AppUser> findAllOrderedById();
}

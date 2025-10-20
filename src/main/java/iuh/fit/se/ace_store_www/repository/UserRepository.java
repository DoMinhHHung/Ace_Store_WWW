package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.User;
import iuh.fit.se.ace_store_www.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    boolean existsByRole(Role role);
}

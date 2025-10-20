package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.Cart;
import iuh.fit.se.ace_store_www.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_IdAndStatus(Long userId, Status status);
    Optional<Cart> findBySessionIdAndStatus(String sessionId, Status status);
    Optional<Cart> findByUser_Id(Long userId);
    Optional<Cart> findBySessionId(String sessionId);
    List<Cart> findByUser_IdAndStatusIs(Long userId, Status status);
    void deleteById(Long id);
    boolean existsByUser_IdAndStatus(Long userId, Status status);
}
package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    void deleteByCartIdAndId(Long cartId, Long id);
}
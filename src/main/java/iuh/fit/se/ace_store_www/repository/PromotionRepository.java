package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findAllByActiveIsTrueAndEndDateAfter(LocalDateTime date);
    Promotion findByName(String name);
}
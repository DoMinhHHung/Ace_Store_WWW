package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
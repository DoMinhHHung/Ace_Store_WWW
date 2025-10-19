package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {
}
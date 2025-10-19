package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.Product;
import iuh.fit.se.ace_store_www.entity.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByStockGreaterThan(Integer stock);
    Product findByName(String name);
    List<Product> findByBrand(String brand);
    List<Product> findAllByProductType(ProductType productType);
}
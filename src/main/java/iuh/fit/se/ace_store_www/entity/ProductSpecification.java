package iuh.fit.se.ace_store_www.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_specifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String specValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
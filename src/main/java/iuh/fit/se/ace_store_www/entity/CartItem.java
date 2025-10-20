package iuh.fit.se.ace_store_www.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", length = 500)
    private String productName;

    @Column(name = "product_price", precision = 19, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "line_total", precision = 19, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "product_main_image", length = 1000)
    private String productMainImage;

    @Column(name = "promotion_id")
    private Long promotionId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void recalcLineTotal() {
        if (productPrice == null || quantity == null) {
            lineTotal = BigDecimal.ZERO;
        } else {
            lineTotal = productPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        }
    }
}

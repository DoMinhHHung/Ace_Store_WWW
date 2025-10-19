package iuh.fit.se.ace_store_www.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promotions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double discountPercent;

    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private LocalDateTime endDate;

    private boolean active = true;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
}

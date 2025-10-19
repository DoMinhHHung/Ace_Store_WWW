package iuh.fit.se.ace_store_www.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromotionResponse {
    private Long id;
    private String name;
    private String description;
    private Double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
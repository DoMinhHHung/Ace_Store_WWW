package iuh.fit.se.ace_store_www.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdatePromotionRequest {
    private String name;
    private String description;
    private Double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
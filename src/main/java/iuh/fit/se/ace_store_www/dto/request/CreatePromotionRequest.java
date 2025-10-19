package iuh.fit.se.ace_store_www.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Data
public class CreatePromotionRequest {
    @NotBlank(message = "Promotion name cannot be empty.")
    private String name;

    private String description;

    @Min(value = 0, message = "Discount must be positive.")
    @Max(value = 1, message = "Discount cannot exceed 100%.")
    private Double discountPercent; // 0.0 -> 1.0 (0% -> 100%)

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
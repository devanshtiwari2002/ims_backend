package com.inventory.imsbackend.sales.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// represent single row in shopping cart
@Data
public class SaleItemRequest {
    @NotNull(message = "Product Id is Required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}

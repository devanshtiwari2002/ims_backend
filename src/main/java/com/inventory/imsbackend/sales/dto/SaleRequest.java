package com.inventory.imsbackend.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class SaleRequest {
    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String customerPhone;

    @NotEmpty(message = "Sale must have at least one item")
    private List<SaleItemRequest> items;
}

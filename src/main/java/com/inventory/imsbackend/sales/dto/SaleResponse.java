package com.inventory.imsbackend.sales.dto;

import com.inventory.imsbackend.sales.enums.SaleStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SaleResponse {
    private Long id;
    private String invoiceNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private LocalDateTime saleDate;
    private SaleStatus status;
    private List<SaleItemResponse> items;
}

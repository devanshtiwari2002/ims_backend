package com.inventory.imsbackend.sales.service;

import com.inventory.imsbackend.product.Product;
import com.inventory.imsbackend.product.ProductRepository;
import com.inventory.imsbackend.sales.dto.*;
import com.inventory.imsbackend.sales.entity.*;
import com.inventory.imsbackend.sales.enums.SaleStatus;
import com.inventory.imsbackend.sales.repository.SaleRepository;
import com.inventory.imsbackend.user.User; // Ensure this import matches your project
import com.inventory.imsbackend.user.UserRepository; // You'll need this to link the seller
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // Added to find the current user

    @Transactional
    public SaleResponse createSale(SaleRequest request) { // Return type changed to DTO

        // 1. Get the current logged-in user (The Seller)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sale sale = new Sale();
        sale.setInvoiceNumber(generateInvoiceNumber());
        sale.setCustomerName(request.getCustomerName());
        sale.setCustomerPhone(request.getCustomerPhone());
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setSeller(seller); // Linking the sale to the staff member

        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found ID: " + itemReq.getProductId()));

            // Validate Stock - Industry Standard: Use a custom exception if possible
            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for: " + product.getName() +
                        " (Available: " + product.getQuantity() + ")");
            }

            BigDecimal subtotal = product.getSellingPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            // Update product stock
            product.setQuantity(product.getQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            SaleItem saleItem = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getSellingPrice())
                    .subtotal(subtotal)
                    .build();

            saleItems.add(saleItem);
        }

        sale.setTotalAmount(totalAmount);
        sale.setItems(saleItems);

        // Save and convert to Response DTO
        return mapToResponse(saleRepository.save(sale));
    }

    // Get all sales (for admin)
    public PaginatedSaleResponse getAllSales(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("saleDate").descending());
        Page<Sale> salePage = saleRepository.findAll(pageable);

        return buildPaginatedResponse(salePage);
    }

    // Get current User's sales (for staff)
    public PaginatedSaleResponse getMySales(int pageNo, int pageSize){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not Found"));

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("saleDate").descending());
        Page<Sale> salePage = saleRepository.findBySeller(seller, pageable);

        return  buildPaginatedResponse(salePage);
    }

    // to generaete pdf geting sales by id
    public SaleResponse getSaleById(Long id){
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with ID: " + id));

        // if user is Staff they should only see there own sales
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Staff can only download there own invoices , admin can downlaod any
        boolean isAdmin = currentUser.getRole().equals("ADMIN");

        if(!isAdmin && !sale.getSeller().getUsername().equals(username)){
            throw new RuntimeException("your are not authorised to view this sale");
        }

        return mapToResponse(sale);
    }

    private PaginatedSaleResponse buildPaginatedResponse(Page<Sale> salePage){
        List<SaleResponse> content = salePage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PaginatedSaleResponse.builder()
                .content(content)
                .pageNo(salePage.getNumber())
                .pageSize(salePage.getSize())
                .totalElements(salePage.getTotalElements())
                .totalPages(salePage.getTotalPages())
                .last(salePage.isLast())
                .build();

    }

    private SaleResponse mapToResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
                .map(item -> SaleItemResponse.builder()
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .invoiceNumber(sale.getInvoiceNumber())
                .customerName(sale.getCustomerName())
                .customerPhone(sale.getCustomerPhone())
                .totalAmount(sale.getTotalAmount())
                .saleDate(sale.getSaleDate())
                .status(sale.getStatus())
                .items(itemResponses)
                .build();
    }

    private String generateInvoiceNumber() {
        String lastInvoice = saleRepository.findLastInvoiceNumber().orElse("INV-2026-0000");
        String[] parts = lastInvoice.split("-");
        int nextId = Integer.parseInt(parts[2]) + 1;
        return String.format("INV-2026-%04d", nextId);
    }
}
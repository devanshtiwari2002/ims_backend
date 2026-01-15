package com.inventory.imsbackend.sales.controller;

import com.inventory.imsbackend.product.dto.PaginatedProductResponse;
import com.inventory.imsbackend.sales.dto.SaleRequest;
import com.inventory.imsbackend.sales.dto.SaleResponse;
import com.inventory.imsbackend.sales.service.SalesService;
import com.inventory.imsbackend.sales.util.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.inventory.imsbackend.sales.dto.PaginatedSaleResponse;

import java.io.IOException;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request){
        return ResponseEntity.ok(salesService.createSale(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<PaginatedSaleResponse> getAllSales(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize ){
        return ResponseEntity.ok(salesService.getAllSales(pageNo, pageSize));
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/my-sales")
    public ResponseEntity<PaginatedSaleResponse> getMySales(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize){
        return ResponseEntity.ok(salesService.getMySales(pageNo, pageSize));
    }

    // allow user to downaload the pdf by id
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void downloadInvoice(@PathVariable Long id, HttpServletResponse response) throws IOException {
        // fethch sale details
        SaleResponse sale = salesService.getSaleById(id);

        // set response headers for PDF
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=invoice_" + sale.getInvoiceNumber() + ".pdf";
        response.setHeader(headerKey, headerValue);

        // Triger PDF Generation
        com.inventory.imsbackend.sales.util.PdfGenerator generator = new com.inventory.imsbackend.sales.util.PdfGenerator();
        generator.generate(sale, response);

    }
}

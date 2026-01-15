package com.inventory.imsbackend.sales.entity;

import com.inventory.imsbackend.sales.enums.SaleStatus;
import com.inventory.imsbackend.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    private String customerName;
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    private BigDecimal totalAmount;
    private LocalDateTime saleDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User seller; // the staff member

    // 2. Industry Standard: Helper method to add items and maintain both sides of the relationship
    @Builder.Default
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    // 2. Industry Standard: Helper method to add items and maintain both sides of the relationship
    public void addSaleItem(SaleItem item) {
        items.add(item);
        item.setSale(this);

//    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SaleItem> items = new ArrayList<>();
    }
}

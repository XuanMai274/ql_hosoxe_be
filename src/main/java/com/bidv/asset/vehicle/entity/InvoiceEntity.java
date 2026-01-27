package com.bidv.asset.vehicle.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class InvoiceEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_id_seq")
    @SequenceGenerator(
            name = "invoice_id_seq",
            sequenceName = "invoice_id_seq"
    )
    private Long id;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "seller_tax_code")
    private String sellerTaxCode;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_tax_code")
    private String buyerTaxCode;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "vat_amount")
    private BigDecimal vatAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<VehicleEntity> vehicles;
}
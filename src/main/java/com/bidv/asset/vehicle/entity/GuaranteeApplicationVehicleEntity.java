package com.bidv.asset.vehicle.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicles_proposal")
public class GuaranteeApplicationVehicleEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ga_vehicle_seq")
    @SequenceGenerator(name = "ga_vehicle_seq",
            sequenceName = "ga_vehicle_seq")
    private Long id;

    // ===== VEHICLE INFO =====
    @Column(name = "vehicle_name")
    private String vehicleName;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "color")
    private String color;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "invoice_number")
    private String invoiceNumber;
    // số hóa đơn NPP xác nhận

    @Column(name = "payment_method")
    private String paymentMethod;
    // Hình thức thanh toán đại lý đăng ký

    @Column(name = "bank_name")
    private String bankName;

    // ===== AMOUNT =====
    @Column(name = "vehicle_price", precision = 18, scale = 2)
    private BigDecimal vehiclePrice;

    @Column(name = "guarantee_amount", precision = 18, scale = 2)
    private BigDecimal guaranteeAmount;

    // ===== RELATION =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarantee_application_id")
    private GuaranteeApplicationEntity guaranteeApplication;
}

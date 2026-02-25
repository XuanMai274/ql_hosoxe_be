package com.bidv.asset.vehicle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "mortgage_contract_sequence")
public class MortgageContractSequenceEntity {

    @Id
    private Long mortgageContractId;

    @Column(name = "guarantee_running_no")
    private Integer guaranteeRunningNo;   // cho HDBLCT

    @Column(name = "warehouse_running_no")
    private Integer warehouseRunningNo;   // cho nhập kho

    @Version
    private Long version; // chống race condition
    @OneToOne
    @MapsId
    @JoinColumn(name = "mortgage_contract_id")
    private MortgageContractEntity mortgageContract;
}

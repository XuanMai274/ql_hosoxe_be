package com.bidv.asset.vehicle.Repository;

import com.bidv.asset.vehicle.entity.InvoiceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends CrudRepository<InvoiceEntity,Long> {
    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);

}

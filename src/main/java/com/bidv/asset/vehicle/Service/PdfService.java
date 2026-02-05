package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.InvoiceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface PdfService {

    // Extract text PDF thường
    InvoiceResponse extractPdf(MultipartFile file) throws IOException;
}
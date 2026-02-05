package com.bidv.asset.vehicle.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExcelService {
    List<Map<String, Object>> extractExcel(MultipartFile file) throws IOException;
}


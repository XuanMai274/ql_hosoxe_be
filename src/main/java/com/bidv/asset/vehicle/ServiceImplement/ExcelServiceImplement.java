package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.Service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelServiceImplement implements ExcelService {

    @Override
    public List<Map<String, Object>> extractExcel(MultipartFile file) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int headerRowNum = -1;
            List<String> headers = new ArrayList<>();

            // Tìm dòng tiêu đề (quét 20 dòng đầu tiên)
            for (int i = 0; i <= Math.min(sheet.getLastRowNum(), 20); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                for (Cell cell : row) {
                    String cellValue = formatter.formatCellValue(cell).toLowerCase().trim();
                    if (cellValue.contains("vin") || cellValue.contains("số khung")) {
                        headerRowNum = i;
                        break;
                    }
                }
                if (headerRowNum != -1) {
                    for (Cell cell : row) {
                        headers.add(formatter.formatCellValue(cell).trim());
                    }
                    break;
                }
            }

            if (headerRowNum == -1)
                return result;

            // Xác định vị trí cột định danh (VIN/Số khung) để biết ranh giới bảng
            int identifierIdx = -1;
            for (int j = 0; j < headers.size(); j++) {
                String h = headers.get(j).toLowerCase();
                if (h.contains("vin") || h.contains("số khung")) {
                    identifierIdx = j;
                    break;
                }
            }

            // Đọc dữ liệu từ sau dòng tiêu đề
            for (int i = headerRowNum + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    break; // Gặp dòng null thì dừng (hết bảng)

                Map<String, Object> rowData = new LinkedHashMap<>();
                boolean hasData = false;
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String val = formatter.formatCellValue(cell);
                    rowData.put(headers.get(j), val);
                    if (val != null && !val.trim().isEmpty())
                        hasData = true;
                }

                if (!hasData)
                    break; // Dòng trống hoàn toàn -> hết bảng

                // Nếu cột Số khung trống -> Có thể là dòng Tổng hoặc bắt đầu bảng khác -> Dừng
                // lại
                if (identifierIdx != -1) {
                    Cell idCell = row.getCell(identifierIdx);
                    String idVal = formatter.formatCellValue(idCell).trim();
                    if (idVal.isEmpty())
                        break;
                }

                result.add(rowData);
            }
        }
        return result;
    }
}

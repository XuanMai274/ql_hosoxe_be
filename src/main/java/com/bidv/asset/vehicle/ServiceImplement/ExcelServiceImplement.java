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

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<String> currentHeaders = null;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                // Kiểm tra xem dòng này có phải là header mới không
                boolean isHeaderCandidate = false;
                List<String> rowValues = new ArrayList<>();
                for (Cell cell : row) {
                    String val = formatter.formatCellValue(cell).trim();
                    rowValues.add(val);
                    String lowerVal = val.toLowerCase();
                    // Các từ khóa nhận diện dòng tiêu đề
                    if (lowerVal.equals("stt") || lowerVal.contains("số khung") ||
                            lowerVal.contains("vin") || lowerVal.contains("ngày hđ") ||
                            lowerVal.contains("hóa đơn htv") || lowerVal.contains("hóa đơn vat") ||
                            lowerVal.contains("số hóa đơn") || lowerVal.equals("hóa đơn")) {
                        isHeaderCandidate = true;
                    }
                }

                // Nếu dòng này có nhiều cột và trông giống header -> Cập nhật header mới
                long nonEmptyCols = rowValues.stream().filter(s -> !s.isEmpty()).count();
                if (isHeaderCandidate && nonEmptyCols >= 3) {
                    currentHeaders = rowValues;
                    continue;
                }

                // Nếu đã thấy header và dòng này có dữ liệu -> Trích xuất
                if (currentHeaders != null && nonEmptyCols > 0) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    boolean meaningfulData = false;
                    for (int j = 0; j < currentHeaders.size(); j++) {
                        String h = currentHeaders.get(j);
                        if (h == null || h.isEmpty())
                            continue; // Bỏ qua cột không có tên header

                        Cell cell = row.getCell(j);
                        String val = formatter.formatCellValue(cell).trim();
                        rowData.put(h, val);
                        if (!val.isEmpty())
                            meaningfulData = true;
                    }

                    // Kiểm tra xem đây có phải dòng "Tổng cộng" không (thường STT trống hoặc chứa
                    // chữ "Tổng")
                    if (meaningfulData) {
                        result.add(rowData);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Lỗi khi xử lý file Excel: " + e.getMessage());
        }
        return result;
    }
}

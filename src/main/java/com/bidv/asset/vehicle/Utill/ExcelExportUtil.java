package com.bidv.asset.vehicle.Utill;

import com.bidv.asset.vehicle.DTO.VehicleExcelDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExcelExportUtil {

    public static byte[] exportVehicleExcel(List<VehicleExcelDTO> data) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách xe");

            String[] headers = {
                    "STT", "Tên xe", "Tên tài sản", "Trạng thái", "Nguồn vốn",
                    "Số khung", "Số máy", "Model", "Màu", "Số chỗ", "Giá xe",
                    "Ngày nhập", "Ngày xuất", "Ngày bàn giao HS",
                    "Bản gốc", "HS nhập", "Số đăng ký", "Mô tả",
                    "Số HĐ", "Ngày HĐ", "Người bán", "MST NB",
                    "Người mua", "MST NM", "Tổng tiền", "VAT",
                    "Số HĐBL", "Ngày HĐBL", "Số TB", "Ngày TB",
                    "Mã tham chiếu", "BL dự kiến", "BL thực tế",
                    "Đã dùng", "Còn lại",
                    "SL xe DK", "SL xe nhập", "SL xe xuất",
                    "HĐ mua bán", "GT HĐ",
                    "Đại diện", "Hãng SX",
                    "Ngày tạo xe", "Ngày tạo BL"
            };

            /* ===== HEADER ===== */
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                sheet.setColumnWidth(i, 20 * 256);
            }

            /* ===== DATA ===== */
            int rowIdx = 1;
            for (VehicleExcelDTO d : data) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;

                setCell(row, col++, d.getStt());
                setCell(row, col++, d.getVehicleName());
                setCell(row, col++, d.getAssetName());
                setCell(row, col++, d.getStatus());
                setCell(row, col++, d.getFundingSource());

                setCell(row, col++, d.getChassisNumber());
                setCell(row, col++, d.getEngineNumber());
                setCell(row, col++, d.getModelType());
                setCell(row, col++, d.getColor());
                setCell(row, col++, d.getSeats());
                setCell(row, col++, d.getPrice());

                setCell(row, col++, d.getImportDate());
                setCell(row, col++, d.getExportDate());
                setCell(row, col++, d.getDocsDeliveryDate());

                setCell(row, col++, d.getOriginalCopy());
                setCell(row, col++, d.getImportDocs());
                setCell(row, col++, d.getRegistrationOrderNumber());
                setCell(row, col++, d.getDescription());

                /* ===== INVOICE ===== */
                setCell(row, col++, d.getInvoiceNumber());
                setCell(row, col++, d.getInvoiceDate());
                setCell(row, col++, d.getSellerName());
                setCell(row, col++, d.getSellerTaxCode());
                setCell(row, col++, d.getBuyerName());
                setCell(row, col++, d.getBuyerTaxCode());
                setCell(row, col++, d.getInvoiceTotalAmount());
                setCell(row, col++, d.getVatAmount());

                /* ===== GUARANTEE ===== */
                setCell(row, col++, d.getGuaranteeContractNumber());
                setCell(row, col++, d.getGuaranteeContractDate());
                setCell(row, col++, d.getGuaranteeNoticeNumber());
                setCell(row, col++, d.getGuaranteeNoticeDate());
                setCell(row, col++, d.getReferenceCode());

                setCell(row, col++, d.getExpectedGuaranteeAmount());
                setCell(row, col++, d.getTotalGuaranteeAmount());
                setCell(row, col++, d.getUsedAmount());
                setCell(row, col++, d.getRemainingAmount());

                setCell(row, col++, d.getExpectedVehicleCount());
                setCell(row, col++, d.getImportedVehicleCount());
                setCell(row, col++, d.getExportedVehicleCount());

                setCell(row, col++, d.getSaleContract());
                setCell(row, col++, d.getSaleContractAmount());

                setCell(row, col++, d.getAuthorizedRepresentativeName());
                setCell(row, col++, d.getManufacturerName());

                setCell(row, col++, d.getVehicleCreatedAt());
                setCell(row, col++, d.getGuaranteeCreatedAt());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Xuất Excel thất bại", e);
        }
    }

    /* ===== CELL HELPER ===== */
    private static void setCell(Row row, int col, Object value) {
        if (value == null) {
            row.createCell(col).setBlank();
        } else if (value instanceof Number) {
            row.createCell(col).setCellValue(((Number) value).doubleValue());
        } else if (value instanceof LocalDate) {
            row.createCell(col).setCellValue(value.toString());
        } else if (value instanceof LocalDateTime) {
            row.createCell(col).setCellValue(value.toString());
        } else {
            row.createCell(col).setCellValue(value.toString());
        }
    }
}

package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GuaranteeApplicationDTO;
import com.bidv.asset.vehicle.DTO.GuaranteeApplicationVehicleDTO;
import com.bidv.asset.vehicle.Repository.GuaranteeApplicationRepository;
import com.bidv.asset.vehicle.Service.GuaranteeApplicationExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class GuaranteeApplicationExportServiceImplement implements GuaranteeApplicationExportService {
    @Autowired
    GuaranteeApplicationRepository repository;

    /* ========================================================= */
    /* ================= FILE 1 - ĐỀ NGHỊ ====================== */
    /* ========================================================= */

    @Override
    public byte[] exportDeNghiCapBaoLanh(Long applicationId) throws Exception {

        GuaranteeApplicationDTO dto = getData(applicationId);

        XWPFDocument doc =
                loadTemplate("/templates/DeNghiCapBaoLanh/de-nghi-cap-bao-lanh.docx");

        Map<String,String> map = buildCommonData(dto);

        replaceAll(doc,map);
        forceTimesNewRoman(doc);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= FILE 2 - DANH SÁCH XE ================= */
    /* ========================================================= */

    @Override
    public byte[] exportDanhSachXeBaoLanh(Long applicationId) throws Exception {

        GuaranteeApplicationDTO dto = getData(applicationId);

        XWPFDocument doc =
                loadTemplate("/templates/DeNghiCapBaoLanh/danh-sach-xe-cap-bao-lanh.docx");

        replaceVehicleTable(doc,dto);

        Map<String,String> map = buildCommonData(dto);

        replaceAll(doc,map);
        forceTimesNewRoman(doc);

        return writeDoc(doc);
    }

    /* ========================================================= */
    /* ================= BUILD COMMON DATA ===================== */
    /* ========================================================= */

    private Map<String,String> buildCommonData(
            GuaranteeApplicationDTO dto) {

        Map<String,String> map = new HashMap<>();

        map.put("{{SO_DE_NGHI}}", safe(dto.getApplicationNumber()));
        map.put("{{NGAY}}", formatDate(LocalDate.now()));

        map.put("{{KHACH_HANG}}",
                dto.getCustomerDTO()!=null ?
                        safe(dto.getCustomerDTO().getCustomerName()) : "");

        map.put("{{HDTD}}",
                dto.getCreditContractDTO()!=null ?
                        safe(dto.getCreditContractDTO().getContractNumber()) : "");

        map.put("{{HDBD}}",
                dto.getMortgageContractDTO()!=null ?
                        safe(dto.getMortgageContractDTO().getContractNumber()) : "");

        map.put("{{TONG_XE}}",
                String.valueOf(dto.getTotalVehicleCount()));

        map.put("{{TONG_BL}}",
                formatMoney(dto.getTotalGuaranteeAmount()));

        if(dto.getTotalGuaranteeAmount()!=null){
            map.put("{{TONG_BL_TEXT}}",
                    VietnameseNumberUtil.toVietnamese(
                            dto.getTotalGuaranteeAmount()));
        }

        return map;
    }

    /* ========================================================= */
    /* ================= TABLE REPLACEMENT ===================== */
    /* ========================================================= */

    private void replaceVehicleTable(
            XWPFDocument doc,
            GuaranteeApplicationDTO dto) {

        for (XWPFTable table : doc.getTables()) {

            for (int i = 0; i < table.getRows().size(); i++) {

                XWPFTableRow row = table.getRow(i);

                if (row.getCell(0) != null &&
                        row.getCell(0).getText()
                                .toLowerCase()
                                .contains("{{stt}}")) {

                    int templateIndex = i;

                    for (int j = 0;
                         j < dto.getVehicles().size();
                         j++) {

                        GuaranteeApplicationVehicleDTO v =
                                dto.getVehicles().get(j);

                        XWPFTableRow newRow =
                                table.insertNewTableRow(templateIndex + j);

                        copyRow(row,newRow);

                        Map<String,String> data =
                                buildVehicleData(v,j+1);

                        replaceRowPlaceholders(newRow,data);
                    }

                    table.removeRow(templateIndex
                            + dto.getVehicles().size());
                    break;
                }
            }
        }
    }

    private Map<String,String> buildVehicleData(
            GuaranteeApplicationVehicleDTO v,
            int stt){

        Map<String,String> map = new HashMap<>();

        map.put("{{stt}}",String.valueOf(stt));
        map.put("{{vehicleType}}",safe(v.getVehicleType()));
        map.put("{{color}}",safe(v.getColor()));
        map.put("{{chassis}}",safe(v.getChassisNumber()));
        map.put("{{invoice}}",safe(v.getInvoiceNumber()));
        map.put("{{price}}",formatMoney(v.getVehiclePrice()));
        map.put("{{gate}}","100");
        map.put("{{guarantee}}",formatMoney(v.getGuaranteeAmount()));

        return map;
    }

    /* ========================================================= */
    /* ================= HELPER ================================ */
    /* ========================================================= */

    private GuaranteeApplicationDTO getData(Long id){
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy hồ sơ"));
    }

    private GuaranteeApplicationDTO mapToDTO(
            com.bidv.asset.vehicle.entity.GuaranteeApplicationEntity e){

        // bạn map theo project của bạn
        return new GuaranteeApplicationDTO();
    }

    private XWPFDocument loadTemplate(String path)
            throws IOException {

        var is = getClass().getResourceAsStream(path);
        if (is == null)
            throw new IOException("Không tìm thấy template: "+path);

        return new XWPFDocument(is);
    }

    private byte[] writeDoc(XWPFDocument doc)
            throws IOException {

        try(ByteArrayOutputStream out =
                    new ByteArrayOutputStream()){

            doc.write(out);
            return out.toByteArray();
        }
    }

    private void replaceAll(
            XWPFDocument doc,
            Map<String,String> data){

        for(XWPFParagraph p : doc.getParagraphs()){
            replaceInParagraph(p,data);
        }

        for(XWPFTable table : doc.getTables()){
            for(XWPFTableRow row : table.getRows()){
                replaceRowPlaceholders(row,data);
            }
        }
    }

    private void replaceRowPlaceholders(
            XWPFTableRow row,
            Map<String,String> data){

        for(XWPFTableCell cell : row.getTableCells()){
            for(XWPFParagraph p : cell.getParagraphs()){
                replaceInParagraph(p,data);
            }
        }
    }

    private void replaceInParagraph(
            XWPFParagraph paragraph,
            Map<String,String> data){

        List<XWPFRun> runs = paragraph.getRuns();
        if(runs==null||runs.isEmpty()) return;

        CTRPr oldRPr = null;
        if(runs.get(0).getCTR().isSetRPr()){
            oldRPr = (CTRPr)
                    runs.get(0).getCTR().getRPr().copy();
        }

        StringBuilder text = new StringBuilder();
        for(XWPFRun r:runs){
            if(r.getText(0)!=null)
                text.append(r.getText(0));
        }

        String replaced = text.toString();
        for(Map.Entry<String,String> e:data.entrySet()){
            replaced = replaced.replace(
                    e.getKey(),e.getValue());
        }

        for(int i=runs.size()-1;i>=0;i--){
            paragraph.removeRun(i);
        }

        XWPFRun newRun = paragraph.createRun();
        if(oldRPr!=null)
            newRun.getCTR().setRPr(oldRPr);

        newRun.setText(replaced);
    }

    private void copyRow(
            XWPFTableRow source,
            XWPFTableRow target){

        for (XWPFTableCell cell :
                source.getTableCells()) {

            XWPFTableCell newCell =
                    target.addNewTableCell();

            newCell.setText(cell.getText());
        }
    }

    private void forceTimesNewRoman(
            XWPFDocument doc){

        for(XWPFParagraph p:doc.getParagraphs()){
            for(XWPFRun r:p.getRuns()){
                r.setFontFamily("Times New Roman");
            }
        }
    }

    private String safe(String s){
        return s==null?"":s;
    }

    private String formatMoney(BigDecimal value){
        if(value==null) return "";
        return NumberFormat
                .getInstance(new Locale("vi","VN"))
                .format(value);
    }

    private String formatDate(LocalDate date){
        if(date==null) return "";
        return String.format("%02d/%02d/%d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
    }

}

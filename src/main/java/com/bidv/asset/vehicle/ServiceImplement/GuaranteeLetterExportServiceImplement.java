package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.ExportDeXuatRequest;
import com.bidv.asset.vehicle.DTO.GuaranteeLetterDTO;
import com.bidv.asset.vehicle.DTO.XuatDeXuatBaoLanh;
import com.bidv.asset.vehicle.Service.GuaranteeLetterExportService;
import com.bidv.asset.vehicle.Utill.VietnameseNumberUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GuaranteeLetterExportServiceImplement implements GuaranteeLetterExportService {
    BigDecimal gHTDConSD= BigDecimal.valueOf(0);
    BigDecimal gHTDaSuDung=BigDecimal.valueOf(0);

    // =====================================================
    // ================= PUBLIC API ========================
    // =====================================================

    @Override
    public byte[] generateThuBaoLanh(GuaranteeLetterDTO dto, String template) throws IOException {
        if ("VINFAST_V1".equals(template)) {
            return generateThuBaoLanhVinfast(dto);
        }
        if ("HYNDAI_V1".equals(template)) {
            return generateThuBaoLanhHyundai(dto);
        }
        throw new IllegalArgumentException("Template không được hỗ trợ: " + template);
    }

    @Override
    public byte[] generateDeXuatBaoLanh(ExportDeXuatRequest exportDeXuatRequest,String template) throws IOException {
        if ("VINFAST_V1".equals(template)) {
            return generateDeXuatVinfast(exportDeXuatRequest.getGuaranteeLetter(),exportDeXuatRequest.getExportData());
        }
        if ("HYNDAI_V1".equals(template)) {
            return generateDeXuatHyundai(exportDeXuatRequest.getGuaranteeLetter(),exportDeXuatRequest.getExportData());
        }
        throw new IllegalArgumentException("Template không được hỗ trợ: " + template);
    }

    @Override
    public byte[] generateXetDuyet(ExportDeXuatRequest exportDeXuatRequest,String template) throws IOException {
        if ("VINFAST_V1".equals(template)) {
            return generateXetDuyetVinfast(exportDeXuatRequest.getGuaranteeLetter(),exportDeXuatRequest.getExportData());
        }
        if ("HYNDAI_V1".equals(template)) {
            return generateXetDuyetHyundai(exportDeXuatRequest.getGuaranteeLetter(),exportDeXuatRequest.getExportData());
        }
        throw new IllegalArgumentException("Template không được hỗ trợ: " + template);
    }

    @Override
    public byte[] generateYKien(GuaranteeLetterDTO dto, String template) throws IOException{
        if ("VINFAST_V1".equals(template)) {
            return generateYKienVinfast(dto);
        }
        if ("HYNDAI_V1".equals(template)) {
            return generateYKienHyndai(dto);
        }
        throw new IllegalArgumentException("Template không được hỗ trợ: " + template);
    }

    // =====================================================
    // ================= THƯ BẢO LÃNH ======================
    // =====================================================

    private byte[] generateThuBaoLanhVinfast(GuaranteeLetterDTO dto) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/Vinfast/thu-bao-lanh-vinfast.docx");
        Map<String, String> data = buildCommonData(dto);
        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }

    private byte[] generateThuBaoLanhHyundai(GuaranteeLetterDTO dto) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/Hyndai/thu-bao-lanh-hyndai.docx");
        Map<String, String> data = buildCommonData(dto);
        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }

    // =====================================================
    // ================= ĐỀ XUẤT BẢO LÃNH ==================
    // =====================================================

    private byte[] generateDeXuatVinfast(GuaranteeLetterDTO dto, XuatDeXuatBaoLanh xuatDeXuatBaoLanhBaoLanh) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/Vinfast/de-xuat-cap-bao-lanh-vinfast.docx");
        gHTDConSD=xuatDeXuatBaoLanhBaoLanh.getRemainingAmount();
        gHTDaSuDung=xuatDeXuatBaoLanhBaoLanh.getTotalGuaranteeAmount();
        Map<String, String> data = buildCommonData(dto);

        BigDecimal expectedAmount = normalizeMoney(dto.getExpectedGuaranteeAmount());
        BigDecimal guaranteeFee = calculateGuaranteeFee(expectedAmount);
        BigDecimal guaranteeFeeSum =
                calculateGuaranteeFee(expectedAmount).add(BigDecimal.valueOf(330000));

        data.put("{{DOCUMENT_TITLE}}", "ĐỀ XUẤT CẤP BẢO LÃNH");
        data.put("{{REQUEST_DATE}}", formatDate(LocalDate.now()));
        data.put("{{GUARANTEE_DATE_TITLE}}", toVietnameseDate(dto.getGuaranteeContractDate()));

        // ===== GHTD =====
//        data.put("{{GHTD}}",
//                formatMoney(dto.getCreditContractDTO().getCreditLimit()));

        // ===== GHTD đã sử dụng =====
        data.put("{{GHTDaSuDung}}",
                formatMoney(dto.getTotalGuaranteeAmount()));

        // ===== Chi tiết =====
        data.put("{{DuNoVay}}",
                formatMoney(dto.getUsedAmount()));

        data.put("{{SoDuCap}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getGuaranteeBalance()));

        data.put("{{SoDuVay}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getShortTermLoanBalance()));

        // ===== GHTD còn lại =====
        data.put("{{GHTDConSD}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getRemainingAmount()));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}", formatMoney(expectedAmount));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG}}", formatMoney(guaranteeFeeSum));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
                VietnameseNumberUtil.toVietnamese(expectedAmount));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI}}",
                formatMoney(guaranteeFee));

        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }

    private byte[] generateDeXuatHyundai(GuaranteeLetterDTO dto, XuatDeXuatBaoLanh xuatDeXuatBaoLanhBaoLanh) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/Hyndai/de-xuat-cap-bao-lanh-hyndai.docx");

        Map<String, String> data = buildCommonData(dto);

        BigDecimal expectedAmount = normalizeMoney(dto.getExpectedGuaranteeAmount());
//        BigDecimal guaranteeFee = calculateGuaranteeFee(expectedAmount);
//        BigDecimal guaranteeFeeSum =
//                calculateGuaranteeFee(expectedAmount).add(BigDecimal.valueOf(330000));

        data.put("{{DOCUMENT_TITLE}}", "ĐỀ XUẤT CẤP BẢO LÃNH");
        data.put("{{REQUEST_DATE}}", formatDate(LocalDate.now()));
        data.put("{{GUARANTEE_DATE_TITLE}}", toVietnameseDate(dto.getGuaranteeContractDate()));

        // ===== GHTD =====
//        data.put("{{GHTD}}",
//                formatMoney(dto.getCreditContractDTO().getCreditLimit()));

        // ===== GHTD đã sử dụng =====
        data.put("{{GHTDaSuDung}}",
                formatMoney(dto.getTotalGuaranteeAmount()));

        // ===== Chi tiết =====
        data.put("{{DuNoVay}}",
                formatMoney(dto.getUsedAmount()));

        data.put("{{SoDuCap}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getGuaranteeBalance()));

        data.put("{{SoDuVay}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getShortTermLoanBalance()));

        // ===== GHTD còn lại =====
        data.put("{{GHTDConSD}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getRemainingAmount()));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}", formatMoney(expectedAmount));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
                VietnameseNumberUtil.toVietnamese(expectedAmount));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI}}",
//                formatMoney(guaranteeFee));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG}}", formatMoney(guaranteeFeeSum));
        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }

    // =====================================================
    // ================= XÉT DUYỆT =========================
    // =====================================================

    private byte[] generateXetDuyetVinfast(GuaranteeLetterDTO dto,XuatDeXuatBaoLanh xuatDeXuatBaoLanhBaoLanh) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/Vinfast/phan-xet-duyet-vinfast.docx");

        Map<String, String> data = buildCommonData(dto);

        BigDecimal expectedAmount = normalizeMoney(dto.getExpectedGuaranteeAmount());
//        BigDecimal guaranteeFee = calculateGuaranteeFee(expectedAmount);
        // ===== GHTD =====
//        data.put("{{GHTD}}",
//                formatMoney(dto.getCreditContractDTO().getCreditLimit()));

        // ===== GHTD đã sử dụng =====
        data.put("{{GHTDaSuDung}}",
                formatMoney(dto.getTotalGuaranteeAmount()));

        // ===== Chi tiết =====
        data.put("{{DuNoVay}}",
                formatMoney(dto.getUsedAmount()));

        data.put("{{SoDuCap}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getGuaranteeBalance()));

        data.put("{{SoDuVay}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getShortTermLoanBalance()));

        // ===== GHTD còn lại =====
        data.put("{{GHTDConSD}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getRemainingAmount()));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}", formatMoney(expectedAmount).replace(" đồng", ""));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
                VietnameseNumberUtil.toVietnamese(expectedAmount));

//        data.put("{{TONGPHIBAOLANH}}", formatMoney(guaranteeFee).replace(" đồng", ""));
//        data.put("{{TONGPHIBAOLANHTEXT}}",
//                VietnameseNumberUtil.toVietnamese(guaranteeFee));

        data.put("{{GUARANTEE_DATE_TITLE}}", toVietnameseDate(LocalDate.now()));

        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }

    private byte[] generateXetDuyetHyundai(GuaranteeLetterDTO dto,XuatDeXuatBaoLanh xuatDeXuatBaoLanhBaoLanh) throws IOException {
        XWPFDocument doc = loadTemplate("/templates/Hyndai/phan-xet-duyet-hyndai.docx");

        Map<String, String> data = buildCommonData(dto);

        BigDecimal expectedAmount = normalizeMoney(dto.getExpectedGuaranteeAmount());
        BigDecimal guaranteeFee = calculateGuaranteeFee(expectedAmount);
        // ===== GHTD =====
//        data.put("{{GHTD}}",
//                formatMoney(dto.getCreditContractDTO().getCreditLimit()));

        // ===== GHTD đã sử dụng =====
        data.put("{{GHTDaSuDung}}",
                formatMoney(dto.getTotalGuaranteeAmount()));

        // ===== Chi tiết =====
        data.put("{{DuNoVay}}",
                formatMoney(dto.getUsedAmount()));

        data.put("{{SoDuCap}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getGuaranteeBalance()));

        data.put("{{SoDuVay}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getShortTermLoanBalance()));

        // ===== GHTD còn lại =====
        data.put("{{GHTDConSD}}",
                formatMoney(xuatDeXuatBaoLanhBaoLanh.getRemainingAmount()));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}", formatMoney(expectedAmount).replace(" đồng", ""));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
                VietnameseNumberUtil.toVietnamese(expectedAmount));

        data.put("{{TONGPHIBAOLANH}}", formatMoney(guaranteeFee).replace(" đồng", ""));
        data.put("{{TONGPHIBAOLANHTEXT}}",
                VietnameseNumberUtil.toVietnamese(guaranteeFee));

        data.put("{{GUARANTEE_DATE_TITLE}}", toVietnameseDate(LocalDate.now()));

        replaceAllPlaceholders(doc, data);
        return writeDoc(doc);
    }
    // =====================================================
    // ================= PHẦN Ý KIÊN ==============================
    // =====================================================
    public byte[] generateYKienVinfast(GuaranteeLetterDTO dto)throws IOException{
        XWPFDocument doc =
                loadTemplate("/templates/Vinfast/y-kien-phong-quan-tri-vinfast.docx");

        Map<String, String> data = buildCommonData(dto);

        BigDecimal expectedAmount =
                normalizeMoney(dto.getExpectedGuaranteeAmount());

        BigDecimal guaranteeFee =
                calculateGuaranteeFee(expectedAmount);
        data.put("{{REQUEST_DATE}}",
                formatDate(LocalDate.now()));

        data.put("{{GUARANTEE_DATE_TITLE}}",
                toVietnameseDate(LocalDate.now()));

        // ===== Số tiền bảo lãnh =====
        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}",
                formatMoney(expectedAmount).replace(" đồng", ""));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
                VietnameseNumberUtil.toVietnamese(expectedAmount));

        // ===== Phí bảo lãnh =====
        data.put("{{TONGPHIBAOLANH}}",
                formatMoney(guaranteeFee).replace(" đồng", ""));

        data.put("{{TONGPHIBAOLANHTEXT}}",
                VietnameseNumberUtil.toVietnamese(guaranteeFee));

        replaceAllPlaceholders(doc, data);

        return writeDoc(doc);
    }
    public byte[] generateYKienHyndai(GuaranteeLetterDTO dto)throws IOException{
        XWPFDocument doc =
                loadTemplate("/templates/Hyndai/y-kien-cua-phong-quan-tri-hyndai.docx");

        Map<String, String> data = buildCommonData(dto);

        BigDecimal expectedAmount =
                normalizeMoney(dto.getExpectedGuaranteeAmount());

        BigDecimal guaranteeFee =
                calculateGuaranteeFee(expectedAmount);
        data.put("{{REQUEST_DATE}}",
                formatDate(LocalDate.now()));

        data.put("{{GUARANTEE_DATE_TITLE}}",
                toVietnameseDate(LocalDate.now()));

        // ===== Số tiền bảo lãnh =====
        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}",
                formatMoney(expectedAmount).replace(" đồng", ""));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
                VietnameseNumberUtil.toVietnamese(expectedAmount));

        // ===== Phí bảo lãnh =====
        data.put("{{TONGPHIBAOLANH}}",
                formatMoney(guaranteeFee).replace(" đồng", ""));

        data.put("{{TONGPHIBAOLANHTEXT}}",
                VietnameseNumberUtil.toVietnamese(guaranteeFee));

        replaceAllPlaceholders(doc, data);

        return writeDoc(doc);
    }
    // =====================================================
    // ================= COMMON DATA =======================
    // =====================================================
    private Map<String, String> buildCommonData(GuaranteeLetterDTO dto) {

        Map<String, String> data = new HashMap<>();

        BigDecimal expectedAmount = normalizeMoney(dto.getExpectedGuaranteeAmount());
        BigDecimal minFee = BigDecimal.valueOf(800000);

        // ===== Fee thường =====
        BigDecimal calculatedFee = calculateGuaranteeFee(expectedAmount);
        BigDecimal guaranteeFee =
                calculatedFee.compareTo(minFee) < 0 ? minFee : calculatedFee;

        String guaranteeFeeNote = "";
        if (calculatedFee.compareTo(minFee) < 0) {
            guaranteeFeeNote =
                    " + Phí phát hành: thu phí phát hành thư bảo lãnh mới theo mức tối thiểu hiện nay là 800.000 đồng / lần";
        }

        BigDecimal guaranteeFeeSum =
                guaranteeFee.add(BigDecimal.valueOf(330000));


        // ===== Fee Hyundai =====
        BigDecimal calculatedFeeHyundai = calculateGuaranteeFee_hyndai(expectedAmount);
        BigDecimal guaranteeFeeHyundai =
                calculatedFeeHyundai.compareTo(minFee) < 0 ? minFee : calculatedFeeHyundai;

        String guaranteeFeeNoteHyundai = "";
        if (calculatedFeeHyundai.compareTo(minFee) < 0) {
            guaranteeFeeNoteHyundai =
                    " + Phí phát hành: thu phí phát hành thư bảo lãnh mới theo mức tối thiểu hiện nay là 800.000 đồng / lần";
        }

        BigDecimal guaranteeFeeSumHyundai =
                guaranteeFeeHyundai.add(BigDecimal.valueOf(330000));


        // ===== Map dữ liệu =====
//        data.put("{{GHTD}}", formatMoney(dto.getCreditContractDTO().getCreditLimit()));
        data.put("{{GHTDConSD}}", formatMoney(gHTDConSD));
        data.put("{{GHTDaSuDung}}", formatMoney(gHTDaSuDung));

        data.put("{{GUARANTEE_NUMBER}}", safe(dto.getGuaranteeContractNumber()));
        data.put("{{GUARANTEE_DATE}}", formatDate(LocalDate.now()));
        data.put("{{GUARANTEE_DATE_TITLE}}", toVietnameseDate(dto.getGuaranteeContractDate()));

        data.put("{{SALE_CONTRACT}}", safe(dto.getSaleContract()));
        data.put("{{SALE_CONTRACT_AMOUNT}}", formatMoney(dto.getSaleContractAmount()));
        data.put("{{GIAHDMB}}", formatMoney(dto.getSaleContractAmount()));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}", formatMoney(expectedAmount));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}", VietnameseNumberUtil.toVietnamese(expectedAmount));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI}}", formatMoney(guaranteeFee));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG}}", formatMoney(guaranteeFeeSum));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG_TEXT}}",
                VietnameseNumberUtil.toVietnamese(guaranteeFeeSum));

        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_HYNDAI}}", formatMoney(guaranteeFeeHyundai));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG_HYNDAI}}", formatMoney(guaranteeFeeSumHyundai));
        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG_TEXT_HYNDAI}}",
                VietnameseNumberUtil.toVietnamese(guaranteeFeeSumHyundai));

        // ===== Note =====
        data.put("{{guaranteeFeeNote}}", safe(guaranteeFeeNote));
        data.put("{{guaranteeFeeNoteHyundai}}", safe(guaranteeFeeNoteHyundai));


        // ===== Đại diện =====
//        if (dto.getBranchAuthorizedRepresentativeDTO() != null) {
//            var rep = dto.getBranchAuthorizedRepresentativeDTO();
//
//            data.put("{{REPRESENTATIVE_NAME}}", safe(rep.getRepresentativeName()));
//            data.put("{{REPRESENTATIVE_TITLE}}", safe(rep.getRepresentativeTitle()));
//            data.put("{{AUTH_DOC_NO}}", safe(rep.getAuthorizationDocNo()));
//            data.put("{{AUTH_DOC_DATE}}",
//                    rep.getAuthorizationDocDate() != null
//                            ? formatDate(rep.getAuthorizationDocDate())
//                            : "");
//            data.put("{{AUTH_ISSUER}}", safe(rep.getAuthorizationIssuer()));
//        }

        data.put("{{EXPECTED_VEHICLE_COUNT}}",
                dto.getExpectedVehicleCount() != null
                        ? dto.getExpectedVehicleCount().toString()
                        : "");

        return data;
    }
//    private Map<String, String> buildCommonData(GuaranteeLetterDTO dto) {
//
//        Map<String, String> data = new HashMap<>();
//        data.put("{{GHTD}}",
//                formatMoney(dto.getCreditContractDTO().getCreditLimit()));
//        data.put("{{GHTDConSD}}",
//                formatMoney(gHTDConSD));
//        // ===== GHTD đã sử dụng =====
//        data.put("{{GHTDaSuDung}}",
//                formatMoney(gHTDaSuDung));
//        BigDecimal expectedAmount = normalizeMoney(dto.getExpectedGuaranteeAmount());
//        // ===== Tính phí bảo lãnh =====
//        BigDecimal calculatedFee = calculateGuaranteeFee(expectedAmount);
//
//        BigDecimal minFee = BigDecimal.valueOf(800000);
//
//        // Biến ghi chú khi áp dụng mức tối thiểu
//        String guaranteeFeeNote = " ";
//
//        // Áp dụng mức tối thiểu
//        BigDecimal guaranteeFee =
//                calculatedFee.compareTo(minFee) < 0 ? minFee : calculatedFee;
//
//        if (calculatedFee.compareTo(minFee) < 0) {
//            guaranteeFeeNote =
//                    " + Phí phát hành: thu phí phát hành thư bảo lãnh mới theo mức tối thiểu hiện nay là 800.000 đồng / lần";
//        }
//        BigDecimal guaranteeFeeSum =
//                calculateGuaranteeFee(expectedAmount).add(BigDecimal.valueOf(330000));
//        BigDecimal calculatedFeeHyundai =
//                calculateGuaranteeFee_hyndai(expectedAmount);
//
//        String guaranteeFeeNoteHyundai = "";
//
//        BigDecimal guaranteeFeeHyndai =
//                calculatedFeeHyundai.compareTo(minFee) < 0 ? minFee : calculatedFeeHyundai;
//
//        if (calculatedFeeHyundai.compareTo(minFee) < 0) {
//            guaranteeFeeNoteHyundai =
//                    " + Phí phát hành: thu phí phát hành thư bảo lãnh mới theo mức tối thiểu hiện nay là 800.000 đồng / lần";
//        }
//        data.put("guaranteeFeeNote",
//                guaranteeFeeNote);
//        data.put("guaranteeFeeNoteHyundai",safe(guaranteeFeeNote));
//        BigDecimal guaranteeFeeSum_hyndai =
//                calculateGuaranteeFee_hyndai(expectedAmount).add(BigDecimal.valueOf(330000));
//        data.put("{{GUARANTEE_NUMBER}}", safe(dto.getGuaranteeContractNumber()));
//        data.put("{{GUARANTEE_DATE}}", formatDate(LocalDate.now()));
//        data.put("{{GUARANTEE_DATE_TITLE}}",
//                toVietnameseDate(dto.getGuaranteeContractDate()));
////        data.put("{{GHTDaSuDung}}",
////                formatMoney(dto.getTotalGuaranteeAmount()));
//        data.put("{{GHTDConSD}}", formatMoney(gHTDConSD));
//        // ===== GHTD đã s dụng =====
//       data.put("{{GHTDaSuDung}}",formatMoney(gHTDaSuDung));
//        data.put("{{SALE_CONTRACT}}", safe(dto.getSaleContract()));
//        data.put("{{SALE_CONTRACT_AMOUNT}}",
//                formatMoney(dto.getSaleContractAmount()));
//        data.put("{{GIAHDMB}}", formatMoney(dto.getSaleContractAmount()));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT}}",
//                formatMoney(expectedAmount));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_TEXT}}",
//                VietnameseNumberUtil.toVietnamese(expectedAmount));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG}}", formatMoney(guaranteeFeeSum));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI}}", formatMoney(guaranteeFee));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG_TEXT}}",VietnameseNumberUtil.toVietnamese(guaranteeFeeSum));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG_HYNDAI}}", formatMoney(guaranteeFeeSum_hyndai));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_HYNDAI}}", formatMoney(guaranteeFeeHyndai));
//        data.put("{{EXPECTED_GUARANTEE_AMOUNT_PHI_TONG_TEXT_HYNDAI}}",VietnameseNumberUtil.toVietnamese(guaranteeFeeSum_hyndai));
//        if (dto.getBranchAuthorizedRepresentativeDTO() != null) {
//            data.put("{{REPRESENTATIVE_NAME}}",
//                    safe(dto.getBranchAuthorizedRepresentativeDTO().getRepresentativeName()));
//            data.put("{{REPRESENTATIVE_TITLE}}",
//                    safe(dto.getBranchAuthorizedRepresentativeDTO().getRepresentativeTitle()));
//            data.put("{{AUTH_DOC_NO}}",
//                    safe(dto.getBranchAuthorizedRepresentativeDTO().getAuthorizationDocNo()));
//            data.put("{{AUTH_DOC_DATE}}",
//                    dto.getBranchAuthorizedRepresentativeDTO().getAuthorizationDocDate() != null
//                            ? formatDate(dto.getBranchAuthorizedRepresentativeDTO().getAuthorizationDocDate())
//                            : "");
//            data.put("{{AUTH_ISSUER}}",
//                    safe(dto.getBranchAuthorizedRepresentativeDTO().getAuthorizationIssuer()));
//        }
//
//        data.put("{{EXPECTED_VEHICLE_COUNT}}",
//                dto.getExpectedVehicleCount() != null
//                        ? dto.getExpectedVehicleCount().toString()
//                        : "");
//
//        return data;
//    }

    // =====================================================
    // ================= TEMPLATE UTIL =====================
    // =====================================================

    private XWPFDocument loadTemplate(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) throw new FileNotFoundException("Không tìm thấy template: " + path);
        return new XWPFDocument(is);
    }

    private void replaceAllPlaceholders(XWPFDocument doc, Map<String, String> data) {
        for (XWPFParagraph p : doc.getParagraphs()) replaceInParagraph(p, data);
        doc.getTables().forEach(t -> t.getRows().forEach(r ->
                r.getTableCells().forEach(c ->
                        c.getParagraphs().forEach(p -> replaceInParagraph(p, data)))));
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            if (run.getText(0) != null) fullText.append(run.getText(0));
        }

        String replaced = fullText.toString();
        for (Map.Entry<String, String> e : data.entrySet()) {
            replaced = replaced.replace(e.getKey(), e.getValue());
        }

        if (!replaced.equals(fullText.toString())) {
            runs.get(0).setText(replaced, 0);
            for (int i = 1; i < runs.size(); i++) runs.get(i).setText("", 0);
        }
    }

    // =====================================================
    // ================= UTIL ==============================
    // =====================================================

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateGuaranteeFee(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.02"))
                .multiply(new BigDecimal("29"))
                .divide(new BigDecimal("365"), 0, RoundingMode.HALF_UP);
    }
    private BigDecimal calculateGuaranteeFee_hyndai(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.02"))
                .multiply(new BigDecimal("60"))
                .divide(new BigDecimal("365"), 0, RoundingMode.HALF_UP);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "";
        return NumberFormat.getInstance(new Locale("vi", "VN"))
                .format(value.setScale(0, RoundingMode.HALF_UP)) + " đồng";
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String toVietnameseDate(LocalDate date) {
        if (date == null) return "";
        return "ngày " + date.getDayOfMonth()
                + " tháng " + date.getMonthValue()
                + " năm " + date.getYear();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private byte[] writeDoc(XWPFDocument doc) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();
        return out.toByteArray();
    }

}

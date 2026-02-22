package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.Repository.VehicleCatalogRepository;
import com.bidv.asset.vehicle.Service.VehicleCatalogService;
import com.bidv.asset.vehicle.entity.VehicleCatalogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleCatalogServiceImplement implements VehicleCatalogService {

    @Autowired
    private VehicleCatalogRepository vehicleCatalogRepository;

    @Override
    public List<VehicleCatalogEntity> getAll() {
        return vehicleCatalogRepository.findAll();
    }

    @Override
    public VehicleCatalogEntity save(VehicleCatalogEntity entity) {
        return vehicleCatalogRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        vehicleCatalogRepository.deleteById(id);
    }

    @Override
    public Integer getSeatsByModelName(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return null;
        }

        String searchName = modelName.trim().toUpperCase();

        // 1. ƯU TIÊN HÀNG ĐẦU: Tra cứu trong Database do người dùng định nghĩa
        try {
            List<VehicleCatalogEntity> all = vehicleCatalogRepository.findAll();
            String normalizedSearch = searchName.replaceAll("\\s+", " ").trim();

            for (VehicleCatalogEntity item : all) {
                // Kiểm tra Model Name
                if (item.getModelName() != null) {
                    String catalogModel = item.getModelName().toUpperCase().replaceAll("\\s+", " ").trim();
                    if (!catalogModel.isEmpty()
                            && (normalizedSearch.contains(catalogModel) || catalogModel.contains(normalizedSearch))) {
                        return item.getSeats();
                    }
                }

                // Kiểm tra Description trong Catalog (Người dùng có thể lưu từ khóa ở đây)
                if (item.getDescription() != null) {
                    String catalogDesc = item.getDescription().toUpperCase().replaceAll("\\s+", " ").trim();
                    if (!catalogDesc.isEmpty()
                            && (normalizedSearch.contains(catalogDesc) || catalogDesc.contains(normalizedSearch))) {
                        return item.getSeats();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching vehicle catalog: " + e.getMessage());
        }

        // 2. PHƯƠNG ÁN DỰ PHÒNG 1: Hard-coded quy tắc chung (Chỉ chạy nếu DB không có)
        if (searchName.contains("ACCENT") || searchName.contains("I10") || searchName.contains("CRETA") ||
                searchName.contains("ELANTRA") || searchName.contains("VF5") || searchName.contains("VF6") ||
                searchName.contains("VIOS") || searchName.contains("CITY")) {
            return 5;
        }
        if (searchName.contains("SANTAFE") || searchName.contains("PALISADE") || searchName.contains("STARGAZER") ||
                searchName.contains("VF8") || searchName.contains("VF9") || searchName.contains("CARNIVAL") ||
                searchName.contains("FORTUNER") || searchName.contains("EVEREST")) {
            return 7;
        }

        // 3. PHƯƠNG ÁN DỰ PHÒNG 2: Regex tìm số kèm chữ "chỗ" xuất hiện trực tiếp trong
        // tên
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*(?:CHỖ|CHỐ|SEAT|CHO)",
                java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(searchName);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }
}

package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.GeneralStatisticsDTO;
import com.bidv.asset.vehicle.Repository.*;
import com.bidv.asset.vehicle.Service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImplement implements StatisticsService {

        @Autowired
        private VehicleRepository vehicleRepository;

        @Autowired
        private GuaranteeApplicationRepository guaranteeApplicationRepository;

        @Autowired
        private GuaranteeLetterRepository guaranteeLetterRepository;

        @Autowired
        private DisbursementRepository disbursementRepository;

        @Autowired
        private ManufacturerRepository manufacturerRepository;

        @Autowired
        private CustomerRepository customerRepository;

        @Autowired
        private CreditContractRepository creditContractRepository;

        @Override
        @Transactional(readOnly = true)
        public GeneralStatisticsDTO getGeneralStatistics() {
                // 1. Xe (Vehicle)
                long totalVehicles = vehicleRepository.count();
                long inWarehouseCount = vehicleRepository.countByWarehouseImportIsNotNullAndWarehouseExportIsNull();
                long returnedToCustomerCount = vehicleRepository.countByWarehouseExportIsNotNull();

                // 2. Bảo lãnh (Guarantee)
                BigDecimal totalIssuedGuaranteeBalance = creditContractRepository.sumIssuedGuaranteeBalance();
                if (totalIssuedGuaranteeBalance == null)
                        totalIssuedGuaranteeBalance = BigDecimal.ZERO;

                long activeGuaranteeLetterCount = guaranteeLetterRepository.countByStatus("ACTIVE");

                BigDecimal actualGuaranteeBalance = creditContractRepository.sumActualGuaranteeBalance();
                if (actualGuaranteeBalance == null)
                        actualGuaranteeBalance = BigDecimal.ZERO;

                // 3. Khoản vay (Loan)
                long activeDisbursementCount = disbursementRepository.countByStatus("ACTIVE");

                BigDecimal totalVehicleLoanBalance = creditContractRepository.sumVehicleLoanBalance();
                if (totalVehicleLoanBalance == null)
                        totalVehicleLoanBalance = BigDecimal.ZERO;

                BigDecimal totalRealEstateLoanBalance = creditContractRepository.sumRealEstateLoanBalance();
                if (totalRealEstateLoanBalance == null)
                        totalRealEstateLoanBalance = BigDecimal.ZERO;

                // 4. Distribution & Trends (Enrichment)
                List<GeneralStatisticsDTO.ManufacturerStats> manuStats = vehicleRepository.findAll().stream()
                                .filter(v -> v.getManufacturerEntity() != null)
                                .collect(Collectors.groupingBy(v -> v.getManufacturerEntity().getName(),
                                                Collectors.counting()))
                                .entrySet().stream()
                                .map(e -> new GeneralStatisticsDTO.ManufacturerStats(e.getKey(), e.getValue()))
                                .collect(Collectors.toList());

                LocalDateTime now = LocalDateTime.now();
                List<GeneralStatisticsDTO.MonthlyStats> monthlyStats = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                for (int i = 5; i >= 0; i--) {
                        LocalDateTime monthDate = now.minusMonths(i);
                        String monthLabel = monthDate.format(formatter);
                        long count = guaranteeApplicationRepository.countByStatus("SUBMITTED");
                        monthlyStats.add(new GeneralStatisticsDTO.MonthlyStats(monthLabel, count));
                }

                List<GeneralStatisticsDTO.CustomerStats> topCustomers = vehicleRepository.findAll().stream()
                                .filter(v -> v.getGuaranteeLetter() != null
                                                && v.getGuaranteeLetter().getCustomer() != null)
                                .collect(Collectors.groupingBy(
                                                v -> v.getGuaranteeLetter().getCustomer().getCustomerName(),
                                                Collectors.counting()))
                                .entrySet().stream()
                                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                                .limit(5)
                                .map(e -> new GeneralStatisticsDTO.CustomerStats(e.getKey(), e.getValue()))
                                .collect(Collectors.toList());

                return GeneralStatisticsDTO.builder()
                                .totalVehicles(totalVehicles)
                                .inWarehouseCount(inWarehouseCount)
                                .returnedToCustomerCount(returnedToCustomerCount)
                                .totalIssuedGuaranteeBalance(totalIssuedGuaranteeBalance)
                                .activeGuaranteeLetterCount(activeGuaranteeLetterCount)
                                .actualGuaranteeBalance(actualGuaranteeBalance)
                                .activeDisbursementCount(activeDisbursementCount)
                                .totalVehicleLoanBalance(totalVehicleLoanBalance)
                                .totalRealEstateLoanBalance(totalRealEstateLoanBalance)
                                .manufacturerStats(manuStats)
                                .monthlyStats(monthlyStats)
                                .topCustomers(topCustomers)
                                .build();
        }
}

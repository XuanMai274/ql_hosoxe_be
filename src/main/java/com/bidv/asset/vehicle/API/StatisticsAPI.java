package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.DTO.GeneralStatisticsDTO;
import com.bidv.asset.vehicle.Service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsAPI {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/general")
    public ResponseEntity<GeneralStatisticsDTO> getGeneralStatistics() {
        return ResponseEntity.ok(statisticsService.getGeneralStatistics());
    }
}

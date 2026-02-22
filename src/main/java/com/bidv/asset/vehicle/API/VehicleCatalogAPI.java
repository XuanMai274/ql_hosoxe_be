package com.bidv.asset.vehicle.API;

import com.bidv.asset.vehicle.Service.VehicleCatalogService;
import com.bidv.asset.vehicle.entity.VehicleCatalogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/officer/vehicle-catalog")
public class VehicleCatalogAPI {

    @Autowired
    private VehicleCatalogService vehicleCatalogService;

    @GetMapping
    public ResponseEntity<List<VehicleCatalogEntity>> getAll() {
        return ResponseEntity.ok(vehicleCatalogService.getAll());
    }

    @PostMapping
    public ResponseEntity<VehicleCatalogEntity> save(@RequestBody VehicleCatalogEntity entity) {
        return ResponseEntity.ok(vehicleCatalogService.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleCatalogService.delete(id);
        return ResponseEntity.ok().build();
    }
}

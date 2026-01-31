//package com.bidv.asset.vehicle.API;
//
//import com.bidv.asset.vehicle.DTO.DocumentDTO;
//import com.bidv.asset.vehicle.Service.VehicleDocumentService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/officer/vehicles/documents")
//@RequiredArgsConstructor
//public class VehicleDocumentAPI {
//
//    private final VehicleDocumentService documentService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<DocumentDTO> upload(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam(required = false) Long vehicleId
//    ) {
//        return ResponseEntity.ok(
//                documentService.(file, vehicleId)
//        );
//    }
//}

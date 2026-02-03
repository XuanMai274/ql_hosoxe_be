package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.OcrResponseDTO;
import com.bidv.asset.vehicle.Service.OcrClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class PythonOcrClientImpl implements OcrClient {

    @Value("${ocr.python.url}")
    private String ocrUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OcrResponseDTO extract(MultipartFile file) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<OcrResponseDTO> response =
                    restTemplate.exchange(
                            ocrUrl + "/extract",
                            HttpMethod.POST,
                            request,
                            OcrResponseDTO.class
                    );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("OCR service error", e);
        }
    }
}

package kr.co.reo.api_sero_click.api;

import kr.co.reo.api_sero_click.api.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/geocoding")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GeocodingController {

    private final GeocodingService geocodingService;

    /**
     * 위도/경도를 주소로 변환 (LLM 기반)
     *
     * GET /api/geocoding?lat=37.545170&lon=126.841120
     * Response: {"address": "서울 강서구 등촌동"}
     */
    @GetMapping
    public Map<String, String> getAddress(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        String address = geocodingService.getAddress(lat, lon);
        return Map.of("address", address);
    }
}

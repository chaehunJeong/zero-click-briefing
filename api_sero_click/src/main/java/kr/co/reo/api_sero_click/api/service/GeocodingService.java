package kr.co.reo.api_sero_click.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.reo.api_sero_click.api.OllamaChatClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LLM 기반 Geocoding 서비스
 * 위도/경도를 주소로 변환 (외부 API 없이 LLM 활용)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingService {

    private final OllamaChatClient llm;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 위도/경도를 한국 주소로 변환
     *
     * 서울 주요 지역 좌표:
     * - 강남구: 37.497~37.529, 127.025~127.065
     * - 강서구: 37.540~37.570, 126.810~126.870
     * - 종로구: 37.570~37.600, 126.970~127.010
     * - 강북구: 37.630~37.660, 127.000~127.040
     */
    public String getAddress(double lat, double lon) {
        try {
            String prompt = String.format("""
                위도 %.6f, 경도 %.6f 좌표를 보고 서울 지역 주소를 추론하세요.

                **서울 주요 지역 좌표 참고**:
                - 강남구: 위도 37.49~37.53, 경도 127.02~127.07
                - 서초구: 위도 37.47~37.51, 경도 127.00~127.04
                - 송파구: 위도 37.50~37.53, 경도 127.08~127.13
                - 강서구: 위도 37.54~37.57, 경도 126.81~126.87
                - 양천구: 위도 37.51~37.54, 경도 126.85~126.90
                - 구로구: 위도 37.48~37.51, 경도 126.85~126.90
                - 종로구: 위도 37.57~37.60, 경도 126.97~127.01
                - 중구: 위도 37.56~37.57, 경도 126.97~127.00
                - 용산구: 위도 37.52~37.55, 경도 126.96~127.01
                - 마포구: 위도 37.54~37.58, 경도 126.90~126.95
                - 서대문구: 위도 37.57~37.59, 경도 126.93~126.97
                - 은평구: 위도 37.59~37.64, 경도 126.91~126.96
                - 강북구: 위도 37.63~37.66, 경도 127.00~127.04
                - 성북구: 위도 37.58~37.61, 경도 127.01~127.05
                - 노원구: 위도 37.64~37.67, 경도 127.05~127.08
                - 동대문구: 위도 37.57~37.59, 경도 127.03~127.06
                - 광진구: 위도 37.53~37.55, 경도 127.07~127.11
                - 성동구: 위도 37.55~37.57, 경도 127.03~127.06
                - 영등포구: 위도 37.51~37.54, 경도 126.89~126.93
                - 동작구: 위도 37.49~37.52, 경도 126.93~126.98
                - 관악구: 위도 37.47~37.49, 경도 126.94~126.97

                **응답 형식** (JSON만 출력):
                {{
                  "city": "서울",
                  "district": "강서구" (위 좌표와 가장 가까운 구),
                  "neighborhood": "등촌동" 또는 "가양동" 또는 "화곡동" (해당 구의 대표 동)
                }}

                반드시 JSON 형식으로만 응답하고, 설명이나 부연 문구는 넣지 마세요.
                """, lat, lon);

            String system = """
                너는 '서울 지역 전문가'다.
                위도/경도 좌표를 보고 서울의 어느 구(區)에 해당하는지 정확히 판단한다.
                JSON 형식으로만 응답한다.
                """;

            log.info("LLM geocoding request: lat={}, lon={}", lat, lon);
            String jsonResponse = llm.chat(system, prompt);
            log.info("LLM geocoding response: {}", jsonResponse);

            // JSON 파싱
            JsonNode root = objectMapper.readTree(jsonResponse);
            String city = root.has("city") ? root.get("city").asText() : "서울";
            String district = root.has("district") ? root.get("district").asText() : "";
            String neighborhood = root.has("neighborhood") ? root.get("neighborhood").asText() : "";

            // 주소 조합
            StringBuilder address = new StringBuilder();
            if (!city.isEmpty()) {
                address.append(city);
            }
            if (!district.isEmpty()) {
                if (address.length() > 0) address.append(" ");
                address.append(district);
            }
            if (!neighborhood.isEmpty()) {
                if (address.length() > 0) address.append(" ");
                address.append(neighborhood);
            }

            String result = address.toString();
            if (result.isEmpty()) {
                result = "서울"; // 기본값
            }

            log.info("Geocoding result: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Geocoding failed", e);
            // LLM 실패 시 좌표 기반 간단 추론
            return fallbackGeocoding(lat, lon);
        }
    }

    /**
     * LLM 실패 시 좌표 범위로 간단히 추론
     */
    private String fallbackGeocoding(double lat, double lon) {
        // 강서구 범위
        if (lat >= 37.54 && lat <= 37.57 && lon >= 126.81 && lon <= 126.87) {
            return "서울 강서구";
        }
        // 강남구 범위
        else if (lat >= 37.49 && lat <= 37.53 && lon >= 127.02 && lon <= 127.07) {
            return "서울 강남구";
        }
        // 종로구 범위
        else if (lat >= 37.57 && lat <= 37.60 && lon >= 126.97 && lon <= 127.01) {
            return "서울 종로구";
        }
        // 기타 서울 지역
        else if (lat >= 37.4 && lat <= 37.7 && lon >= 126.8 && lon <= 127.2) {
            return "서울";
        }
        // 서울 외 지역
        else {
            return "대한민국";
        }
    }
}

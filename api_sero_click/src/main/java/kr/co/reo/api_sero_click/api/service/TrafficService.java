package kr.co.reo.api_sero_click.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.reo.api_sero_click.api.OllamaChatClient;
import kr.co.reo.api_sero_click.model.TrafficResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * LLM 기반 교통 정보 생성 서비스
 * 실제 API 호출 대신 LLM이 시간대와 요일을 고려한 현실적인 교통 데이터를 생성합니다.
 */
@Slf4j
@Service
public class TrafficService {

    private final OllamaChatClient llm;
    private final ObjectMapper objectMapper;

    public TrafficService(OllamaChatClient llm) {
        this.llm = llm;
        this.objectMapper = new ObjectMapper();
        log.info("LLM-based TrafficService initialized (zero API cost!)");
    }

    /**
     * LLM을 사용하여 현실적인 교통 정보 생성 (자동차 + 대중교통)
     */
    public TrafficResponse getTrafficInfo(double lat, double lon) {
        try {
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            int dayOfWeek = now.getDayOfWeek().getValue(); // 1=월요일, 7=일요일
            String dayType = (dayOfWeek >= 1 && dayOfWeek <= 5) ? "평일" : "주말";

            // 출퇴근 시간대 판단
            boolean isRushHour = (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19);
            String timeContext = isRushHour ? "출퇴근 시간대" : "일반 시간대";

            // 시간대별 교통 상황 가이드 (더 현실적인 범위로 조정)
            String carGuide, transitGuide;
            if (dayType.equals("주말")) {
                carGuide = "자동차: 원활, 25~30분";
                transitGuide = "대중교통: 보통, 40~45분, 환승 0~1회";
            } else if (hour >= 7 && hour <= 9) {
                carGuide = "자동차: 혼잡/정체, 45~55분";
                transitGuide = "대중교통: 매우 혼잡함, 42~50분, 환승 1~2회";
            } else if (hour >= 17 && hour <= 19) {
                carGuide = "자동차: 혼잡/지체, 40~50분";
                transitGuide = "대중교통: 혼잡함, 45~52분, 환승 1회";
            } else if (hour >= 10 && hour <= 16) {
                carGuide = "자동차: 원활/서행, 28~35분";
                transitGuide = "대중교통: 쾌적함, 38~45분, 환승 0~1회";
            } else {
                carGuide = "자동차: 원활, 22~28분";
                transitGuide = "대중교통: 쾌적함, 35~42분, 환승 0~1회";
            }

            String prompt = String.format("""
                현재 시각: %s %d시 (%s)
                경로: 서울 강남 → 서울역 (약 18km)

                교통 상황 가이드를 참고하여 JSON을 생성하세요:
                - 자동차: %s
                - 대중교통: %s

                중요 규칙:
                1. 대중교통은 자동차보다 5~10분 더 걸립니다
                2. 대중교통 혼잡도는 시간대와 연관: 출근시간 = "매우 혼잡함", 평상시 = "쾌적함"
                3. 가이드의 시간 범위를 정확히 따르세요

                JSON 형식으로만 응답하세요:
                {
                  "car": {
                    "duration": 가이드 범위 내 숫자,
                    "congestion": "원활 또는 서행 또는 지체 또는 정체",
                    "route": "강남대로 또는 한강대로 또는 테헤란로"
                  },
                  "transit": {
                    "duration": car보다 5~10분 더 긴 시간,
                    "congestion": "쾌적함 또는 보통 또는 혼잡함 또는 매우 혼잡함",
                    "route": "지하철 2호선 또는 지하철 4호선 또는 지하철 1호선",
                    "transfers": 0, 1, 또는 2
                  },
                  "distance": 17
                }
                """, dayType, hour, timeContext, carGuide, transitGuide);

            String system = "당신은 교통 데이터 생성 전문가입니다. JSON 형식으로만 정확하게 응답하세요.";
            String jsonResponse = llm.chat(system, prompt);

            // JSON 파싱
            jsonResponse = extractJson(jsonResponse);
            Map<String, Object> trafficData = objectMapper.readValue(jsonResponse, Map.class);

            // 자동차 정보
            Map<String, Object> carData = (Map<String, Object>) trafficData.get("car");
            int carDuration = parseInt(carData.get("duration"));
            String carCongestion = String.valueOf(carData.get("congestion"));
            String carRoute = String.valueOf(carData.get("route"));

            // 대중교통 정보
            Map<String, Object> transitData = (Map<String, Object>) trafficData.get("transit");
            int transitDuration = parseInt(transitData.get("duration"));
            String transitCongestion = String.valueOf(transitData.get("congestion"));
            String transitRoute = String.valueOf(transitData.get("route"));
            int transfers = parseInt(transitData.get("transfers"));

            int distance = parseInt(trafficData.get("distance"));

            log.info("LLM generated traffic - Car: {}분 {}, Transit: {}분 {} (환승 {}회)",
                     carDuration, carCongestion, transitDuration, transitCongestion, transfers);

            return new TrafficResponse(
                carDuration, carCongestion, carRoute,
                transitDuration, transitCongestion, transitRoute, transfers,
                distance
            );

        } catch (Exception e) {
            log.error("Failed to generate traffic with LLM, using fallback", e);
            return getFallbackTraffic();
        }
    }

    /**
     * JSON 추출 (LLM이 추가 텍스트를 포함할 경우 대비)
     */
    private String extractJson(String response) {
        response = response.trim();
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }

    private int parseInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 30; // 기본값
        }
    }

    /**
     * LLM 실패 시 폴백 교통 데이터
     */
    private TrafficResponse getFallbackTraffic() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        boolean isRushHour = (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19);

        if (isRushHour) {
            // 출근 시간: 자동차 50분, 대중교통 47분 (지하철이 더 빠름)
            return new TrafficResponse(
                50, "정체", "강남대로",
                47, "매우 혼잡함", "지하철 2호선", 1,
                17
            );
        } else {
            // 평상시: 자동차 30분, 대중교통 40분
            return new TrafficResponse(
                30, "원활", "한강대로",
                40, "쾌적함", "지하철 4호선", 0,
                17
            );
        }
    }
}

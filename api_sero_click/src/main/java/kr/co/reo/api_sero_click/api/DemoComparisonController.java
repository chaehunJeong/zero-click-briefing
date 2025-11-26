package kr.co.reo.api_sero_click.api;

import kr.co.reo.api_sero_click.api.service.WeatherService;
import kr.co.reo.api_sero_click.model.WeatherResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 데모용 실시간 데이터 검증 대시보드
 * 실제 날씨 vs LLM 생성 날씨 비교
 */
@RestController
@RequestMapping("/demo")
@CrossOrigin(origins = "*")
public class DemoComparisonController {

    private final WeatherService weatherService;
    private final RestTemplate restTemplate;

    public DemoComparisonController(WeatherService weatherService) {
        this.weatherService = weatherService;
        this.restTemplate = new RestTemplate();
    }

    /**
     * 실제 서울 날씨 vs LLM 생성 날씨 비교
     *
     * GET /demo/weather-comparison
     */
    @GetMapping("/weather-comparison")
    public Map<String, Object> compareWeather() {
        LocalDateTime now = LocalDateTime.now();

        // 1. LLM 생성 날씨
        WeatherResponse llmWeather = weatherService.getWeatherInfo(37.5665, 126.9780);

        // 2. 실제 서울 날씨 (wttr.in API 사용)
        Map<String, Object> realWeather = getRealWeatherFromWttr();

        // 3. 차이 계산
        double tempDiff = 0;
        int humidityDiff = 0;

        if (realWeather.containsKey("temperature")) {
            tempDiff = Math.abs(llmWeather.getTemperature() - (Double) realWeather.get("temperature"));
        }
        if (realWeather.containsKey("humidity")) {
            humidityDiff = Math.abs(llmWeather.getHumidity() - (Integer) realWeather.get("humidity"));
        }

        // 4. 정확도 점수 계산
        double accuracyScore = calculateAccuracy(tempDiff, humidityDiff);

        return Map.of(
            "timestamp", now.toString(),
            "location", "서울",
            "llm", Map.of(
                "temperature", llmWeather.getTemperature(),
                "feelsLike", llmWeather.getFeelsLike(),
                "humidity", llmWeather.getHumidity(),
                "condition", llmWeather.getCondition(),
                "description", llmWeather.getDescription()
            ),
            "real", realWeather,
            "comparison", Map.of(
                "temperatureDiff", String.format("%.1f°C", tempDiff),
                "humidityDiff", humidityDiff + "%",
                "accuracyScore", String.format("%.1f%%", accuracyScore),
                "verdict", getVerdict(accuracyScore)
            ),
            "analysis", Map.of(
                "isRealistic", accuracyScore >= 70,
                "explanation", getExplanation(tempDiff, humidityDiff, accuracyScore)
            )
        );
    }

    /**
     * 실제 서울 날씨 가져오기 (wttr.in API)
     */
    private Map<String, Object> getRealWeatherFromWttr() {
        try {
            String url = "https://wttr.in/Seoul?format=j1";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("current_condition")) {
                @SuppressWarnings("unchecked")
                var currentCondition = ((java.util.List<Map<String, Object>>) response.get("current_condition")).get(0);

                double temp = Double.parseDouble(currentCondition.get("temp_C").toString());
                double feelsLike = Double.parseDouble(currentCondition.get("FeelsLikeC").toString());
                int humidity = Integer.parseInt(currentCondition.get("humidity").toString());

                @SuppressWarnings("unchecked")
                String condition = ((java.util.List<Map<String, String>>) currentCondition.get("weatherDesc"))
                    .get(0).get("value");

                return Map.of(
                    "temperature", temp,
                    "feelsLike", feelsLike,
                    "humidity", humidity,
                    "condition", condition,
                    "source", "wttr.in"
                );
            }
        } catch (Exception e) {
            System.err.println("실제 날씨 조회 실패: " + e.getMessage());
        }

        return Map.of(
            "error", "실제 날씨 데이터를 가져올 수 없습니다",
            "note", "LLM 생성 데이터만 표시됩니다"
        );
    }

    /**
     * 정확도 점수 계산
     */
    private double calculateAccuracy(double tempDiff, int humidityDiff) {
        // 기온 차이: ±5도 이내 = 만점, 10도 = 0점
        double tempScore = Math.max(0, 100 - (tempDiff / 10.0 * 100));

        // 습도 차이: ±20% 이내 = 만점, 40% = 0점
        double humidityScore = Math.max(0, 100 - (humidityDiff / 40.0 * 100));

        // 평균 점수
        return (tempScore * 0.7 + humidityScore * 0.3);
    }

    /**
     * 평가 메시지
     */
    private String getVerdict(double score) {
        if (score >= 90) return "🌟 매우 정확함";
        if (score >= 75) return "✅ 정확함";
        if (score >= 60) return "⚠️ 보통";
        return "❌ 부정확";
    }

    /**
     * 상세 설명
     */
    private String getExplanation(double tempDiff, int humidityDiff, double score) {
        StringBuilder sb = new StringBuilder();

        if (tempDiff <= 3) {
            sb.append("기온 예측이 매우 정확합니다 (±3°C 이내). ");
        } else if (tempDiff <= 5) {
            sb.append("기온 예측이 합리적입니다 (±5°C 이내). ");
        } else {
            sb.append("기온 차이가 다소 큽니다. ");
        }

        if (humidityDiff <= 15) {
            sb.append("습도 예측도 정확합니다.");
        } else {
            sb.append("습도는 약간 차이가 있습니다.");
        }

        sb.append(String.format(" 전체 정확도: %.1f%%", score));

        return sb.toString();
    }

    /**
     * 시스템 정보
     */
    @GetMapping("/info")
    public Map<String, Object> getSystemInfo() {
        return Map.of(
            "app", "Zero-Click Briefing System",
            "version", "1.0.0",
            "features", java.util.List.of(
                "LLM 기반 날씨 생성 (비용 $0)",
                "LLM 기반 교통 정보 생성",
                "스마트 패턴 학습 (집 위치 자동 파악)",
                "자연스러운 한국어 브리핑",
                "iPhone/Apple Watch 알림"
            ),
            "tech_stack", Map.of(
                "backend", "Spring Boot + Ollama",
                "frontend", "Flutter (iOS/Android/Web)",
                "llm", "qwen2.5:3b (로컬)",
                "database", "H2 in-memory"
            ),
            "cost", Map.of(
                "external_api", "$0/월",
                "llm", "$0/월 (로컬 Ollama)",
                "total", "$0/월"
            )
        );
    }
}

package kr.co.reo.api_sero_click.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {
    private String condition;      // 날씨 상태 (맑음, 흐림 등)
    private double temperature;    // 실제 온도
    private double feelsLike;      // 체감 온도
    private int humidity;          // 습도
    private String description;    // 상세 설명
}

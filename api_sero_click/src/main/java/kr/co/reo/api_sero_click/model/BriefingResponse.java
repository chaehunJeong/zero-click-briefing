package kr.co.reo.api_sero_click.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BriefingResponse {
    private boolean shouldNotify;      // 알림 전송 여부
    private String reason;             // 판단 이유
    private double confidence;         // AI 판단 확신도 (0.0-1.0)
    private String predictedTime;      // 예상 출발 시간
    private String briefingText;       // AI 생성 브리핑 문장
}

package kr.co.reo.api_sero_click.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrafficResponse {
    // 자동차 정보
    private int carDuration;          // 자동차 소요 시간 (분)
    private String carCongestion;     // 자동차 혼잡도 (원활, 지체, 정체)
    private String carRoute;          // 자동차 추천 경로

    // 대중교통 정보
    private int transitDuration;      // 대중교통 소요 시간 (분)
    private String transitCongestion; // 대중교통 혼잡도 (쾌적함, 혼잡함 등)
    private String transitRoute;      // 대중교통 경로 (지하철 노선, 버스 등)
    private int transfers;            // 환승 횟수

    // 공통 정보
    private int distance;             // 거리 (km)

    // 전체 생성자
    public TrafficResponse(int carDuration, String carCongestion, String carRoute,
                          int transitDuration, String transitCongestion, String transitRoute, int transfers,
                          int distance) {
        this.carDuration = carDuration;
        this.carCongestion = carCongestion;
        this.carRoute = carRoute;
        this.transitDuration = transitDuration;
        this.transitCongestion = transitCongestion;
        this.transitRoute = transitRoute;
        this.transfers = transfers;
        this.distance = distance;
    }
}

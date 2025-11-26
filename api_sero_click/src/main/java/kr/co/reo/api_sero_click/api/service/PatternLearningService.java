package kr.co.reo.api_sero_click.api.service;

import kr.co.reo.api_sero_click.model.PatternData;
import kr.co.reo.api_sero_click.repository.PatternRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatternLearningService {

    private final PatternRepository patternRepository;

    public PatternLearningService(PatternRepository patternRepository) {
        this.patternRepository = patternRepository;
    }
    
    /**
     * 사용자 패턴 데이터 저장
     */
    public void savePattern(PatternData data) {
        if (data.getUserId() == null || data.getUserId().isEmpty()) {
            data.setUserId("default_user");
        }
        patternRepository.save(data);
    }
    
    /**
     * 사용자의 모든 패턴 데이터 조회
     */
    public List<PatternData> getUserPatterns(String userId) {
        return patternRepository.findByUserId(userId);
    }
    
    /**
     * 특정 시간대의 패턴 분석
     */
    public List<PatternData> getPatternsByTimeRange(String userId, int startHour, int endHour) {
        return patternRepository.findByUserIdAndHourBetween(userId, startHour, endHour);
    }
}

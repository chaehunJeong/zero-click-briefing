
package kr.co.reo.api_sero_click.repository;

import kr.co.reo.api_sero_click.model.PatternData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatternRepository extends JpaRepository<PatternData, Long> {

    List<PatternData> findByUserId(String userId);
    
    List<PatternData> findByUserIdAndHourBetween(String userId, int startHour, int endHour);
    
    List<PatternData> findByUserIdAndCreatedAtAfter(String userId, LocalDateTime date);
    
    List<PatternData> findByUserIdAndDayOfWeek(String userId, int dayOfWeek);
}

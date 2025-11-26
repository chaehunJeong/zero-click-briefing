package kr.co.reo.api_sero_click.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_patterns")
public class PatternData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userId;

    @Column(name = "pattern_timestamp")
    private LocalDateTime timestamp;

    private double latitude;
    private double longitude;

    @Column(name = "hour_of_day")
    private int hour;          // 0-23

    private int dayOfWeek;     // 1-7 (월-일)
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timestamp != null) {
            hour = timestamp.getHour();
            dayOfWeek = timestamp.getDayOfWeek().getValue();
        }
    }
}

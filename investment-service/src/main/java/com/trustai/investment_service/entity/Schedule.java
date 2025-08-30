package com.trustai.investment_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Entity
@Table(name = "schedules") // ReturnSchedule
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String scheduleName;

    //private int scheduleInHour;
    @Column(nullable = false)
    private int intervalMinutes; // e.g., 1440 = 1 day, 10080 = 1 week

    private String description;

    public Duration getIntervalDuration() {
        return Duration.ofMinutes(this.intervalMinutes);
    }
}

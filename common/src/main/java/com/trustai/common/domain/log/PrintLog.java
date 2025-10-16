package com.trustai.common.domain.log;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrintLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private LogType logType;

    @Column(columnDefinition = "TEXT")
    private String logContent;

    private LocalDateTime createdAt;

    public enum LogType {
        INCOME_HISTORY,
    }

}

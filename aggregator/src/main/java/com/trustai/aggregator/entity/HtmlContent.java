package com.trustai.aggregator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "html_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HtmlContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String html;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String css;
}

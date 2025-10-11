package com.trustai.aggregator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "slides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String caption;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slider_id")
    @JsonBackReference
    private Slider slider;
}

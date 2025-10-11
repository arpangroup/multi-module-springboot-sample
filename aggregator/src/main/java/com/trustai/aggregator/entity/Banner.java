package com.trustai.aggregator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String type = "default"; // [hero, primary, top, featured, default, carousel, slot1]

    @Column(length = 1000)
    private String description;

    private String link;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image; // Can store Base64 string or image URL
}

package com.arpangroup.config_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "config_properties",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_config_key_app_profile",
                columnNames = {"config_key", "application", "profile"}
        )
)
@Data
@NoArgsConstructor
public class ConfigProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Id
    @Column(name = "config_key", nullable = false)
    private String key;
    @Column(name = "config_value")
    private String value;

    @Column(nullable = false)
    private String application;
    @Column(nullable = false)
    private String profile;

    private String label;


    @Column(name = "value_type") // optional
    private String valueType;

    @Column(name = "enum_values") // comma-separated
    private String enumValues;

    private String info;

    public ConfigProperty(String key, Object value) {
        this.key = key;
        this.value = String.valueOf(value);
        this.valueType = detectValueType(value).name();
    }

    public ConfigProperty(String key, Object value, String enumValues) {
        this(key, value);
        this.enumValues = enumValues;
    }

    public enum ValueType {
        INT,
        FLOAT,
        DOUBLE,
        BIG_DECIMAL,
        BOOLEAN,
        STRING
    }

    private ValueType detectValueType(Object value) {
        if (value instanceof Integer) return ValueType.INT;
        if (value instanceof Float) return ValueType.FLOAT;
        if (value instanceof Double) return ValueType.DOUBLE;
        if (value instanceof java.math.BigDecimal) return ValueType.BIG_DECIMAL;
        if (value instanceof Boolean) return ValueType.BOOLEAN;
        return ValueType.STRING;
    }
}
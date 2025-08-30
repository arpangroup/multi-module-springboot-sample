package com.trustai.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHierarchyDto {
    private Long id;
    private Long ancestor;
    private Long descendant;
    private int depth;
    private boolean active;
}

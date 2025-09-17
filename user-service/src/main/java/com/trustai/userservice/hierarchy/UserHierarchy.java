package com.trustai.userservice.hierarchy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_hierarchy", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ancestor", "descendant", "depth"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHierarchy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ancestor;
    private Long descendant;
    private int depth;
    private boolean active = false;

    public UserHierarchy(Long ancestor, Long descendant, int depth) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.depth = depth;
        this.active = true;
    }
}

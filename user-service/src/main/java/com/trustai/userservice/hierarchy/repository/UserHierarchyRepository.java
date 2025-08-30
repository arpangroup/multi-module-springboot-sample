package com.trustai.userservice.hierarchy.repository;

import com.trustai.userservice.hierarchy.UserHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHierarchyRepository extends JpaRepository<UserHierarchy, Long> {
    List<UserHierarchy> findByAncestor(Long ancestor);
    List<UserHierarchy> findByDescendant(Long descendant);

    @Query("SELECT uh FROM UserHierarchy uh WHERE uh.ancestor = :ancestor AND uh.depth = :depth")
    List<UserHierarchy> findByAncestorAndDepth(Long ancestor, int depth);

    @Query("SELECT uh FROM UserHierarchy uh WHERE uh.ancestor IN (" +
            "SELECT descendant FROM UserHierarchy WHERE ancestor = :rootUserId AND depth <= :maxDepth) " +
            "AND uh.depth = 1")
    List<UserHierarchy> findByRootAncestorAndDepthLessThanEqual(@Param("rootUserId") Long rootUserId, @Param("maxDepth") int maxDepth);

    @Query("SELECT uh FROM UserHierarchy uh WHERE uh.ancestor = :ancestor AND uh.depth <= :maxDepth")
    List<UserHierarchy> findByAncestorAndDepthLessThanEqual(@Param("ancestor") Long ancestor, @Param("maxDepth") int maxDepth);

    @Query("SELECT uh FROM UserHierarchy uh WHERE uh.ancestor IN (" +
            "SELECT descendant FROM UserHierarchy WHERE ancestor = :rootUserId AND depth <= :maxDepth) " +
            "AND uh.depth = 1")
    List<UserHierarchy> findEdgesForTree(@Param("rootUserId") Long rootUserId, @Param("maxDepth") int maxDepth);

    @Query("""
    SELECT uh FROM UserHierarchy uh
    WHERE uh.ancestor IN (
        SELECT u.descendant FROM UserHierarchy u
        WHERE u.ancestor = :rootUserId AND u.depth <= :maxDepth
    )
    AND uh.depth = 1
""")
    List<UserHierarchy> findDepth1EdgesInSubtree(@Param("rootUserId") Long rootUserId,
                                                 @Param("maxDepth") int maxDepth);

}

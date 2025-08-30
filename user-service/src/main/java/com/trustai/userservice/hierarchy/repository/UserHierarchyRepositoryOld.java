package com.trustai.userservice.hierarchy.repository;

import com.trustai.userservice.hierarchy.UserHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHierarchyRepositoryOld extends JpaRepository<UserHierarchy, Long> {
    List<UserHierarchy> findByAncestor(Long ancestor);
    List<UserHierarchy> findByDescendant(Long descendant);

    @Query("SELECT uh FROM UserHierarchy uh WHERE uh.ancestor = :ancestor AND uh.depth = :depth")
    List<UserHierarchy> findByAncestorAndDepth(Long ancestor, int depth);

    @Query("SELECT uh FROM UserHierarchy uh WHERE uh.ancestor IN :ancestors AND uh.depth = :depth")
    List<UserHierarchy> findByAncestorsAndDepth(List<Long> ancestors, int depth);

    /**
     * Counts the number of downline users at each specified depth level for the given ancestor user.
     *
     * @param userId the ID of the ancestor user whose downline counts are being queried
     * @param depths a list of depth levels to filter the counts by (e.g., [1, 2, 3])
     * @return a list of {@link DownlineCount} objects, each containing:
     *         - depth: the level of downline relative to the ancestor
     *         - userCount: the number of users at that depth
     *
     * Example return list:
     * [
     *   { depth: 1, userCount: 3 },  // three users at depth 1 (direct downline)
     *   { depth: 2, userCount: 5 },  // five users at depth 2
     *   { depth: 3, userCount: 2 }   // two users at depth 3
     * ]
     */
    @Query(value = """
        SELECT uh.depth as depth, COUNT(*) as userCount
        FROM user_hierarchy uh
        WHERE uh.ancestor = :userId AND uh.depth IN :depths
        GROUP BY uh.depth
        ORDER BY uh.depth
    """, nativeQuery = true)
    //List<DownlineCount> countDownlineGroupedByDepth(@Param("userId") Long userId, @Param("depths") List<Integer> depths);
    List<Object[]> countDownlineGroupedByDepth(@Param("userId") Long userId);


    /**
     * Retrieves downline user IDs grouped by their depth levels relative to the given ancestor user.
     * Only depths included in the provided list will be returned.
     *
     * The user IDs at each depth level are concatenated into a single comma-separated string.
     *
     * @param userId the ID of the ancestor user whose downline is being queried
     * @param depths a list of depth levels to filter the downline users by (e.g., [1, 2, 3])
     * @return a list of {@link DownlineUserIds} objects, each containing:
     *         - depth: the level of downline relative to the ancestor
     *         - userIds: a comma-separated string of descendant user IDs at that depth
     *
     * Example return list:
     * [
     *   { depth: 1, userIds: "10,11,12" },  // direct downline users
     *   { depth: 2, userIds: "15,16" },     // second level downline users
     *   { depth: 3, userIds: "20" }         // third level downline users
     * ]
     */
    @Query(value = """
        SELECT depth, GROUP_CONCAT(descendant) AS userIds
        FROM user_hierarchy
        WHERE ancestor = :userId AND depth IN :depths
        GROUP BY depth
        ORDER BY depth
    """, nativeQuery = true)
    //List<DownlineUserIds> findDownlineUserIdsGrouped(@Param("userId") Long userId, @Param("depths") List<Integer> depths);
    List<Object[]> findDownlineUserIdsGrouped(@Param("userId") Long userId);


    /**
     * Retrieves all upline ancestor user IDs of the specified user,
     * ordered from the closest ancestor (direct upline) to the farthest (root).
     *
     * @param userId the ID of the descendant user whose upline ancestors are to be fetched
     * @return a list of ancestor user IDs ordered by descending depth,
     *         where the first element is the closest upline ancestor.
     *
     * Example return list (ordered by depth descending):
     * [7, 3, 1]
     *
     * Explanation:
     * - 7 is the direct upline ancestor (depth = 1)
     * - 3 is the next level upline ancestor (depth = 2)
     * - 1 is the root ancestor (depth = 3)
     */
    @Query(value = """
        SELECT ancestor
        FROM user_hierarchy
        WHERE descendant = :userId AND depth > 0
        ORDER BY depth DESC
    """, nativeQuery = true)
    List<Long> findUplineUserIds(@Param("userId") Long userId);




    /**
     * Retrieves all upline ancestors of a given user along with their depth levels,
     * ordered from the closest ancestor (depth = 1) up to the root ancestor.
     *
     * @param userId the ID of the descendant user whose upline hierarchy is requested
     * @return a list of {@link UplineProjection} containing ancestor user IDs and their
     *         corresponding depth relative to the given user
     *
     * Example return list (ordered by depth ascending):
     * [
     *   { depth: 1, ancestor: 7 },   // direct upline
     *   { depth: 2, ancestor: 3 },   // second level upline
     *   { depth: 3, ancestor: 1 }    // root upline
     * ]
     */
    @Query(value = """
        SELECT depth, ancestor
        FROM user_hierarchy
        WHERE descendant = :userId AND depth > 0
        ORDER BY depth ASC
    """, nativeQuery = true)
    //List<UplineProjection> findUplineDepthAndAncestors(@Param("userId") Long userId);
    List<Object[]> findUplineDepthAndAncestors(@Param("userId") Long userId);
}

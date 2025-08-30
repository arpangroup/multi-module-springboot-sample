package com.trustai.rank_service.service;

import com.trustai.common.dto.UserInfo;
import com.trustai.rank_service.dto.EvaluationReport;
import com.trustai.rank_service.entity.RankConfig;

import java.util.Optional;

public interface RankEvaluatorService {
    /**
     * Performs an actual evaluation of the user's rank and returns the best matched rank they currently qualify for.
     *
     * <p>This method is intended to be used in rank update workflows where the system may update a user's rank
     * based on their current performance metrics and eligibility.</p>
     *
     * <p>Evaluation process:
     * <ul>
     *     <li>Fetches all active ranks sorted by descending priority (highest to lowest).</li>
     *     <li>Checks each rank against all configured specifications.</li>
     *     <li>Selects the highest rank where all specifications pass.</li>
     *     <li>Prevents rank downgrade: if the user already holds a higher rank, it is retained.</li>
     *     <li>If no rank qualifies, falls back to the lowest rank (e.g., RANK_0).</li>
     * </ul>
     *
     * @param user the user to evaluate
     * @return an Optional containing the matched RankConfig, or empty if user metrics are unavailable
     */
    Optional<RankConfig> evaluate(UserInfo user);  // Applies rank

    /**
     * Performs a non-intrusive evaluation of the user's potential rank based on current metrics.
     *
     * <p>This method is designed to provide a **preview** of the highest rank a user can qualify for
     * without making any changes to their current rank. It is useful for:
     * <ul>
     *     <li>Admin dashboards and reporting tools that show what rank a user would earn if evaluated today.</li>
     *     <li>Explaining rank eligibility to users (e.g., “You're missing X criteria to become Silver”).</li>
     *     <li>Debugging and testing the rank evaluation logic with full visibility into rule failures or passes.</li>
     * </ul>
     *
     * <p>Features:
     * <ul>
     *     <li>Evaluates all active ranks in descending order of priority.</li>
     *     <li>Collects detailed results of all specification checks per rank.</li>
     *     <li>Applies downgrade protection logic — prevents a lower rank from being selected if the user already holds a higher one.</li>
     * </ul>
     *
     * @param user the user whose rank is to be previewed
     * @return EvaluationReport containing the matched rank (if any), spec results, and downgrade flag
     */
    EvaluationReport previewEvaluation(UserInfo user);  // Dry run
}

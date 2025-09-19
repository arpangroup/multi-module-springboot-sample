package com.trustai.rank_service.service;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.rank_service.config.RankConfigProperty;
import com.trustai.rank_service.dto.EvaluationReport;
import com.trustai.rank_service.dto.RankEvaluationResultDTO;
import com.trustai.rank_service.dto.SpecificationResult;
import com.trustai.rank_service.entity.RankConfig;
import com.trustai.rank_service.evaluation.RankSpecification;
import com.trustai.rank_service.repository.RankConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankEvaluatorServiceImpl implements RankEvaluatorService {
    private final RankConfigRepository rankRepo;
    private final List<RankSpecification> specifications;
    private final UserApi userApi;
    private final RankConfigProperty rankConfigProperty;

    /*public Optional<RankConfig> evaluateOld(UserInfo user) {
        UserMetrics metrics = userClient.computeMetrics(user.getId());
        if (metrics == null) {
            log.warn("⚠️ UserMetrics is null for userId: {}", user.getId());
            return Optional.empty();
        }

        List<RankConfig> ranks = rankRepo.findAllByActiveTrueOrderByRankOrderDesc();
        RankConfig bestMatched = null;

        for (RankConfig rank : ranks) {
            log.info("🔍 Evaluating rank: {} ({}) for userId: {}", rank.getDisplayName(), rank.getCode(), user.getId());

            List<SpecificationResult> results = specifications.stream()
                    .map(spec -> spec.evaluate(user, metrics, rank))
                    .toList();

            results.forEach(result -> log.info(" - [{}] Spec check: {}", rank.getCode(), result));

            boolean allPassed = results.stream().allMatch(SpecificationResult::isSatisfied);

            if (allPassed) {
                bestMatched = rank; // update best matched rank
            } else {
                // If no bestMatched yet, continue to check lower ranks
                // If bestMatched already found, break early because no need to check lower ranks
                if (bestMatched != null) {
                    log.info("❌ Rank NOT matched: {} ({}), stopping further evaluation.", rank.getDisplayName(), rank.getCode());
                    break;
                }
                // else no bestMatched yet, so continue checking next (lower) rank
                log.info("❌ Rank NOT matched: {} ({}), checking next lower rank.", rank.getDisplayName(), rank.getCode());
            }

        }

        // Prevent downgrade
        String currentRankCode = user.getRankCode();
        if (currentRankCode != null) {
            RankConfig currentRank = ranks.stream()
                    .filter(r -> r.getCode().equals(currentRankCode))
                    .findFirst()
                    .orElse(null);

            if (currentRank != null) {
                if (bestMatched == null || currentRank.getRankOrder() > bestMatched.getRankOrder()) {
                    log.info("🔒 Preventing downgrade: keeping current rank {} ({}), ignoring lower/equal rank.",
                            currentRank.getDisplayName(), currentRank.getCode());
                    return Optional.of(currentRank);
                }
            }
        }


        // Fallback to RANK_0 if no other rank matched
        if (bestMatched == null && !ranks.isEmpty()) {
            // Return the lowest rank (last in descending order)
            bestMatched = ranks.stream()
                    .min(Comparator.comparingInt(RankConfig::getRankOrder))
                    .orElse(null);
            log.info("ℹ️ No rank matched. Falling back to lowest rank: {} ({})", bestMatched.getDisplayName(), bestMatched.getCode());
        }

        if (bestMatched != null) {
            log.info("✅ Rank matched: {} ({})", bestMatched.getDisplayName(), bestMatched.getCode());
        } else {
            log.info("❌ No rank matched for user {}", user.getId());
        }
        return Optional.ofNullable(bestMatched);
    }*/

    @Override
    public Optional<RankConfig> evaluate(UserInfo user) {
        log.info("Starting rank evaluation for userId={}", user.getId());

        UserMetrics metrics = userApi.computeMetrics(user.getId());
        if (metrics == null) {
            log.warn("⚠️ UserMetrics is null for userId: {}", user.getId());
            return Optional.empty();
        }
        log.debug("Computed user metrics for userId={}: {}", user.getId(), metrics);

        List<RankConfig> ranks = rankRepo.findAllByActiveTrueOrderByRankOrderDesc();
        if (ranks.isEmpty()) {
            log.warn("⚠️ No active ranks configured. Cannot evaluate rank for userId: {}", user.getId());
            return Optional.empty();
        }

        Map<RankConfig, List<SpecificationResult>> evaluationMap = new LinkedHashMap<>();
        List<RankConfig> passedRanks = new ArrayList<>();

        // evaluate all ranks
        for (RankConfig rank : ranks) {
            List<SpecificationResult> results = specifications.stream()
                    .map(spec -> spec.evaluate(user, metrics, rank))
                    //.peek(result -> log.info(" - [{}] Spec check: {}", rank.getCode(), result))
                    .toList();

            evaluationMap.put(rank, results);

            if (results.stream().allMatch(SpecificationResult::isSatisfied)) {
                passedRanks.add(rank);
                if (rankConfigProperty.isPreferHighestQualified()) {
                    // ✅ stop early if we want the topmost match
                    log.debug("✅ Highest qualified rank found: {} ({})", rank.getDisplayName(), rank.getCode());
                    break;
                }
            }

        }

        RankConfig bestMatched;
        if (rankConfigProperty.isPreferHighestQualified()) {
            // if flag is true, we either broke at first pass or nothing passed
            bestMatched = passedRanks.isEmpty() ? null : passedRanks.getFirst();
        } else {
            // bottom-up → last one that passed (lowest)
            bestMatched = passedRanks.isEmpty() ? null : passedRanks.getLast();
        }

        // 🔥 Print aggregated log
        logRankEvaluationSummary(user, evaluationMap, bestMatched);


        // Prevent downgrade
        Optional<RankConfig> currentRankOpt = ranks.stream()
                .filter(r -> r.getCode().equals(user.getRankCode()))
                .findFirst();

        // ✅ Apply downgrade prevention only if feature is enabled
        if (rankConfigProperty.isPreventDowngrade() && currentRankOpt.isPresent()) {
            RankConfig currentRank = currentRankOpt.get();
            if (bestMatched == null || currentRank.getRankOrder() > bestMatched.getRankOrder()) {
                log.info("🔒 Preventing downgrade: keeping current rank {} ({}).", currentRank.getDisplayName(), currentRank.getCode());
                return Optional.of(currentRank);
            }
        }

        // Fallback
        if (bestMatched == null) {
            bestMatched = ranks.stream()
                    .min(Comparator.comparingInt(RankConfig::getRankOrder))
                    .orElse(null);
            if (bestMatched != null) {
                log.info("ℹ️ No rank matched. Falling back to lowest: {} ({})", bestMatched.getDisplayName(), bestMatched.getCode());
            }
        }

        log.info("Completed rank evaluation for userId={}. Best matched rank: {} ({})", user.getId(),
                bestMatched != null ? bestMatched.getDisplayName() : "none",
                bestMatched != null ? bestMatched.getCode() : "none");
        return Optional.ofNullable(bestMatched);
    }


    private void logRankEvaluationSummary(UserInfo user,
                                          Map<RankConfig, List<SpecificationResult>> evaluationMap,
                                          RankConfig bestMatched) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n====== FINAL RANK EVALUATION ======\n")
                .append("▶️ UserId: ").append(user.getId()).append("\n");

        for (Map.Entry<RankConfig, List<SpecificationResult>> entry : evaluationMap.entrySet()) {
            RankConfig rank = entry.getKey();
            List<SpecificationResult> results = entry.getValue();
            boolean allPassed = results.stream().allMatch(SpecificationResult::isSatisfied);

            sb.append("\nRank: ").append(rank.getDisplayName())
                    .append(" (").append(rank.getCode()).append(")")
                    .append(" => ").append(allPassed ? "✅ PASSED" : "❌ FAILED").append("\n");

            for (SpecificationResult res : results) {
                sb.append("   - ").append(res).append("\n");
            }
        }

        sb.append("\n🏆 Final Best Matched Rank: ")
                .append(bestMatched != null ? bestMatched.getDisplayName() + " (" + bestMatched.getCode() + ")" : "NONE")
                .append("\n==================================\n");

        log.info(sb.toString());
    }

    @Override
    public EvaluationReport previewEvaluation(UserInfo user) {
        log.info("Starting preview evaluation for userId={}", user.getId());
        UserMetrics metrics = userApi.computeMetrics(user.getId());
        if (metrics == null) {
            log.warn("⚠️ UserMetrics is null for userId: {}", user.getId());
            return new EvaluationReport(null, List.of(), false);
        }
        log.debug("Computed user metrics for userId={}: {}", user.getId(), metrics);


        List<RankConfig> ranks = rankRepo.findAllByActiveTrueOrderByRankOrderDesc();
        RankConfig bestMatched = null;
        List<SpecificationResult> lastSpecResults = List.of();
        boolean downgradePrevented = false;

        for (RankConfig rank : ranks) {
            List<SpecificationResult> results = specifications.stream()
                    .map(spec -> spec.evaluate(user, metrics, rank))
                    .toList();

            boolean allPassed = results.stream().allMatch(SpecificationResult::isSatisfied);
            log.info("Preview evaluating rank: {} ({}), all specs passed: {}", rank.getDisplayName(), rank.getCode(), allPassed);

            if (allPassed) {
                bestMatched = rank;
                lastSpecResults = results;
            } else if (bestMatched != null) {
                log.info("Stopping further preview evaluation after rank: {} ({})", rank.getDisplayName(), rank.getCode());
                break;
            }
        }

        // Prevent downgrade
        String currentRankCode = user.getRankCode();
        if (currentRankCode != null) {
            RankConfig currentRank = ranks.stream()
                    .filter(r -> r.getCode().equals(currentRankCode))
                    .findFirst()
                    .orElse(null);

            if (currentRank != null && (bestMatched == null || currentRank.getRankOrder() > bestMatched.getRankOrder())) {
                bestMatched = currentRank;
                downgradePrevented = true;
                lastSpecResults = List.of(); // No need to show failing specs for downgrade prevention
                log.info("Downgrade prevented in preview evaluation for userId={}: keeping current rank {} ({})", user.getId(), currentRank.getDisplayName(), currentRank.getCode());
            }
        }

        log.info("Completed preview evaluation for userId={}. Best matched rank: {} ({}), downgradePrevented={}",
                user.getId(),
                bestMatched != null ? bestMatched.getDisplayName() : "none",
                bestMatched != null ? bestMatched.getCode() : "none",
                downgradePrevented);
        return new EvaluationReport(bestMatched, lastSpecResults, downgradePrevented);
    }



    public RankEvaluationResultDTO evaluateAndUpdateRank(Long userId) {
        log.info("Starting evaluateAndUpdateRank for userId={}", userId);

        UserInfo user = userApi.getUserById(userId);
        if (user == null) {
            log.warn("User not found for userId={}", userId);
            return new RankEvaluationResultDTO(userId, null, null, false, "User not found");
        }

        String oldRankCode = user.getRankCode(); // assuming you store rankCode
        log.debug("Old rank for userId={}: {}", userId, oldRankCode);

        Optional<RankConfig> matchedRank = evaluate(user);
        if (matchedRank.isPresent() && !matchedRank.get().getCode().equals(oldRankCode)) {
            log.info("Rank upgrade detected for userId={}: {} -> {}", userId, oldRankCode, matchedRank.get().getCode());
            userApi.updateRank(userId, matchedRank.get().getCode()); // persist the new rank
            return new RankEvaluationResultDTO(userId, oldRankCode, matchedRank.get().getCode(), true, "Rank upgraded");
        }

        log.info("No rank upgrade for userId={}. Keeping current rank: {}", userId, oldRankCode);
        return new RankEvaluationResultDTO(userId, oldRankCode, oldRankCode, false, "No upgrade criteria met");
    }

    public List<RankEvaluationResultDTO> evaluateAndUpdateRanks(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            log.info("UserIds list empty or null, fetching all users.");
            userIds = userApi.getUsers().stream().map(UserInfo::getId).toList();
        } else {
            log.info("Evaluating ranks for provided userIds list of size={}", userIds.size());
        }
        List<RankEvaluationResultDTO> results = userIds.stream()
                .map(this::evaluateAndUpdateRank)
                .toList();

        log.info("Completed batch rank evaluation for {} users", results.size());
        return results;
    }


    /*private boolean isEligible(User user, UserMetrics metrics, RankConfig config) {
        log.info("🔍 Evaluating rank: {} ({})", config.getDisplayName(), config.getCode());
//        return specifications.stream()
//                .allMatch(spec -> spec.isSatisfied(user, metrics, config));

        List<SpecificationResult> results = specifications.stream()
                .map(spec -> spec.evaluate(user, metrics, config))
                .toList();

        //results.forEach(result -> log.info("Rank check: {}", result));
        results.forEach(result -> {
            log.info(" - [{}] Spec check: {}", config.getCode(), result);
        });

        return results.stream().allMatch(SpecificationResult::isSatisfied);
    }*/
}

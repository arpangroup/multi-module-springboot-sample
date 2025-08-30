package com.trustai.rank_service.service;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.rank_service.dto.EvaluationReport;
import com.trustai.rank_service.dto.RankEvaluationResultDTO;
import com.trustai.rank_service.dto.SpecificationResult;
import com.trustai.rank_service.entity.RankConfig;
import com.trustai.rank_service.evaluation.RankSpecification;
import com.trustai.rank_service.repository.RankConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankEvaluatorServiceImpl implements RankEvaluatorService {
    private final RankConfigRepository rankRepo;
    private final List<RankSpecification> specifications;
    private final UserApi userClient;

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
        UserMetrics metrics = userClient.computeMetrics(user.getId());
        if (metrics == null) {
            log.warn("⚠️ UserMetrics is null for userId: {}", user.getId());
            return Optional.empty();
        }

        List<RankConfig> ranks = rankRepo.findAllByActiveTrueOrderByRankOrderDesc();
        if (ranks.isEmpty()) {
            log.warn("⚠️ No active ranks configured. Cannot evaluate rank for userId: {}", user.getId());
            return Optional.empty();
        }

        RankConfig bestMatched = null;
        for (RankConfig rank : ranks) {
            log.info("🔍 Evaluating rank: {} ({}) for userId: {}", rank.getDisplayName(), rank.getCode(), user.getId());

            boolean allPassed = specifications.stream()
                    .map(spec -> spec.evaluate(user, metrics, rank))
                    .peek(result -> log.info(" - [{}] Spec check: {}", rank.getCode(), result))
                    .allMatch(SpecificationResult::isSatisfied);

            if (allPassed) {
                bestMatched = rank;
            } else if (bestMatched != null) {
                log.info("❌ Rank NOT matched: {} ({}), stopping further evaluation.", rank.getDisplayName(), rank.getCode());
                break;
            } else {
                log.info("❌ Rank NOT matched: {} ({}), checking next lower rank.", rank.getDisplayName(), rank.getCode());
            }
        }

        // Prevent downgrade
        Optional<RankConfig> currentRankOpt = ranks.stream()
                .filter(r -> r.getCode().equals(user.getRankCode()))
                .findFirst();

        if (currentRankOpt.isPresent()) {
            RankConfig currentRank = currentRankOpt.get();
            if (bestMatched == null || currentRank.getRankOrder() > bestMatched.getRankOrder()) {
                log.info("🔒 Preventing downgrade: keeping current rank {} ({}).", currentRank.getDisplayName(), currentRank.getCode());
                return Optional.of(currentRank);
            }
        }

        // Fallback to lowest rank
        if (bestMatched == null) {
            bestMatched = ranks.stream()
                    .min(Comparator.comparingInt(RankConfig::getRankOrder))
                    .orElse(null);
            log.info("ℹ️ No rank matched. Falling back to lowest rank: {} ({})", bestMatched.getDisplayName(), bestMatched.getCode());
        }

        return Optional.ofNullable(bestMatched);
    }

    @Override
    public EvaluationReport previewEvaluation(UserInfo user) {
        UserMetrics metrics = userClient.computeMetrics(user.getId());
        if (metrics == null) {
            return new EvaluationReport(null, List.of(), false);
        }

        List<RankConfig> ranks = rankRepo.findAllByActiveTrueOrderByRankOrderDesc();
        RankConfig bestMatched = null;
        List<SpecificationResult> lastSpecResults = List.of();
        boolean downgradePrevented = false;

        for (RankConfig rank : ranks) {
            List<SpecificationResult> results = specifications.stream()
                    .map(spec -> spec.evaluate(user, metrics, rank))
                    .toList();

            boolean allPassed = results.stream().allMatch(SpecificationResult::isSatisfied);

            if (allPassed) {
                bestMatched = rank;
                lastSpecResults = results;
            } else if (bestMatched != null) {
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
            }
        }

        return new EvaluationReport(bestMatched, lastSpecResults, downgradePrevented);
    }



    public RankEvaluationResultDTO evaluateAndUpdateRank(Long userId) {
        UserInfo user = userClient.getUserById(userId);

        String oldRankCode = user.getRankCode(); // assuming you store rankCode
        Optional<RankConfig> matchedRank = evaluate(user);

        if (matchedRank.isPresent() && !matchedRank.get().getCode().equals(oldRankCode)) {
            userClient.updateRank(userId, matchedRank.get().getCode()); // persist the new rank
            return new RankEvaluationResultDTO(userId, oldRankCode, matchedRank.get().getCode(), true, "Rank upgraded");
        }

        return new RankEvaluationResultDTO(userId, oldRankCode, oldRankCode, false, "No upgrade criteria met");
    }

    public List<RankEvaluationResultDTO> evaluateAndUpdateRanks(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            userIds = userClient.getUsers().stream().map(UserInfo::getId).toList();
        }
        return userIds.stream()
                .map(this::evaluateAndUpdateRank)
                .toList();
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

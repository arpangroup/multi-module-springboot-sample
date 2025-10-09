package com.trustai.aggregator.service;

import com.trustai.aggregator.dto.NftItem;
import com.trustai.common.lifecycle.Reloadable;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.repository.SchemaRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Getter
@RequiredArgsConstructor
@RefreshScope
@Slf4j
public class NftServiceCache implements Reloadable {
    private final SchemaRepository schemaRepository;
    private volatile List<NftItem> nfts = List.of();
    private static final List<String> OWNER_NAMES = List.of("CryptoPunks", "MetaHeroes", "PixelCats", "MoonApes", "GalaxyKnights");

    @Value("${app.config.currency.symbol:$}")
    String currencySymbol;

    @Value("${app.config.currency.unit:USDT}")
    String currencyUnit;

    @Override
    public void reload() {
        preload();
    }

    @PostConstruct
    public void preload() {
        log.info("Preloading nfts into cache...");
        /*this.nfts = schemaRepository
                .findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(this::mapToNft)
                .toList();*/

        this.nfts = schemaRepository.findAll()
                .stream()//
                .sorted((a, b) -> {
                    BigDecimal specialPrice = BigDecimal.valueOf(99999);

                    // Push items with stakePrice = 99999 to the end
                    if (a.getStakePrice().compareTo(specialPrice) == 0 && b.getStakePrice().compareTo(specialPrice) != 0) return 1; // a goes after b
                    if (a.getStakePrice().compareTo(specialPrice) != 0 && b.getStakePrice().compareTo(specialPrice) == 0) return -1; // a goes before b

                    // Otherwise, fallback to descending id
                    return b.getId().compareTo(a.getId());
                })
                .map(this::mapToNft)
                .toList();
    }

    private NftItem mapToNft(InvestmentSchema schema) {
        BigDecimal stakePrice = schema.getStakePrice() == null ? schema.getMinimumInvestmentAmount() : schema.getStakePrice();

        // Generate random timeLeft between 10 and 200
        int timeLeft = ThreadLocalRandom.current().nextInt(10, 201); // 201 is exclusive

        // Generate random changes between -10 and 80
        int changes = ThreadLocalRandom.current().nextInt(-10, 81); // 81 is exclusive

        return new NftItem(
                schema.getImageUrl(),
                schema.getName(),
                schema.getName(),
                schema.getSchemaBadge(),
                null,
                getRandomOwnerName(),
                stakePrice.toString(),
                timeLeft + "d Left",
                changes + "%",
                currencySymbol,
                currencyUnit
        );
    }

    private String getRandomOwnerName() {
        int index = ThreadLocalRandom.current().nextInt(OWNER_NAMES.size());
        return OWNER_NAMES.get(index);
    }
}

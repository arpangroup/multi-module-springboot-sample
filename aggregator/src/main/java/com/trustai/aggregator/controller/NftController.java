package com.trustai.aggregator.controller;

import com.trustai.aggregator.dto.NftItem;
import com.trustai.aggregator.service.NftServiceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/nfts")
@RequiredArgsConstructor
public class NftController {
    private final NftServiceCache nftServiceCache;

    @GetMapping
    public List<NftItem> getCollections() {
        return nftServiceCache.getNfts();
    }

}

package com.trustai.aggregator.data;

import com.trustai.aggregator.entity.Banner;
import com.trustai.aggregator.repository.BannerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class BannerInitializer {
    private final BannerRepository bannerRepository;

    @PostConstruct
    public void init() {
        getBanners().forEach(banner -> {
            if (!bannerRepository.existsByTitle(banner.getTitle())) {
                bannerRepository.save(banner);
            }
        });
    }

    private List<Banner> getBanners() {
        return List.of(
            new Banner(
                    null,
                    "TrustAI",
                    "default",
                    "Explore the next miracle of NFT.",
                    "",
                    "https://api.trustai.co.in/images/stake_11.png"
            ),
            new Banner(
                    null,
                    "Exclusive Drop",
                    "primary",
                    "Collect rare NFTs today. Limited time only!",
                    "",
                    "https://api.trustai.co.in/images/stake_10.png"
            ),
            new Banner(
                    null,
                    "Smart Trade Now",
                    "featured",
                    "Explore, Discover and Earn Big with one of the top Web3 NFT Marketplaces in the world",
                    "",
                    "https://api.trustai.co.in/images/stake_10.png"
            )
        );
    }

}

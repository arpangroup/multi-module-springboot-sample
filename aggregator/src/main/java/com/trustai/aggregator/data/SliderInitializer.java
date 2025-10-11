package com.trustai.aggregator.data;

import com.trustai.aggregator.entity.Slide;
import com.trustai.aggregator.entity.Slider;
import com.trustai.aggregator.repository.SliderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SliderInitializer {
    private final SliderRepository sliderRepository;

    @PostConstruct
    public void init() {
        getSliders().forEach(slider -> {
            if (!sliderRepository.existsByName(slider.getName())) {
                slider.getSlides().forEach(slide -> slide.setSlider(slider));
                sliderRepository.save(slider);
            }
        });
    }


    private List<Slider> getSliders() {
        return List.of(
                Slider.builder()
                        .name("Homepage Hero Slider")
                        .description("Main homepage promotional carousel.")
                        .slides(List.of(
                                Slide.builder()
                                        .title("Discover Web3")
                                        .caption("Step into the next era of decentralized innovation.")
                                        .link("https://trustai.co.in/web3")
                                        .imageUrl("https://api.trustai.co.in/images/slider_1a.png")
                                        .build(),
                                Slide.builder()
                                        .title("Invest Smarter")
                                        .caption("AI-driven insights for smarter crypto decisions.")
                                        .link("https://trustai.co.in/invest")
                                        .imageUrl("https://api.trustai.co.in/images/slider_1b.png")
                                        .build(),
                                Slide.builder()
                                        .title("NFT Universe")
                                        .caption("Collect rare NFTs with TrustAI marketplace.")
                                        .link("https://trustai.co.in/nft")
                                        .imageUrl("https://api.trustai.co.in/images/slider_1c.png")
                                        .build()
                        ))
                        .build(),

                Slider.builder()
                        .name("Product Showcase")
                        .description("Featured projects and collections.")
                        .slides(List.of(
                                Slide.builder()
                                        .title("Staking Plans")
                                        .caption("Earn daily rewards from our flexible staking options.")
                                        .imageUrl("https://api.trustai.co.in/images/slider_2a.png")
                                        .build(),
                                Slide.builder()
                                        .title("Referral Program")
                                        .caption("Invite and earn — grow your community rewards.")
                                        .imageUrl("https://api.trustai.co.in/images/slider_2b.png")
                                        .build()
                        ))
                        .build(),

                Slider.builder()
                        .name("Mobile App Promo")
                        .description("Showcase TrustAI mobile app features.")
                        .slides(List.of(
                                Slide.builder()
                                        .title("Download the App")
                                        .caption("Stay connected anywhere with the TrustAI mobile app.")
                                        .imageUrl("https://api.trustai.co.in/images/slider_3a.png")
                                        .link("https://trustai.co.in/app")
                                        .build(),
                                Slide.builder()
                                        .title("Instant Notifications")
                                        .caption("Get live updates and trading signals instantly.")
                                        .imageUrl("https://api.trustai.co.in/images/slider_3b.png")
                                        .build()
                        ))
                        .build()
        );
    }
}

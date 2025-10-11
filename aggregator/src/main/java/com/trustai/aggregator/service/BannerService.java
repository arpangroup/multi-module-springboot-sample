package com.trustai.aggregator.service;

import com.trustai.aggregator.entity.Banner;
import com.trustai.aggregator.repository.BannerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {
    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<Banner> getAll() {
        return bannerRepository.findAll();
    }

    public Banner getById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found"));
    }

    public Banner create(Banner banner) {
        banner.setId(null);
        return bannerRepository.save(banner);
    }

    public Banner update(Long id, Banner updated) {
        Banner existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLink(updated.getLink());
        existing.setImage(updated.getImage());
        return bannerRepository.save(existing);
    }

    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}

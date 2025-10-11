package com.trustai.aggregator.controller;

import com.trustai.aggregator.entity.Banner;
import com.trustai.aggregator.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/banners")
//@CrossOrigin(origins = "*") // Allow frontend access
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    @GetMapping
    public List<Banner> getAll() {
        return bannerService.getAll();
    }

    @GetMapping("/{id}")
    public Banner getById(@PathVariable Long id) {
        return bannerService.getById(id);
    }

    @PostMapping
    public Banner create(@RequestBody Banner banner) {
        return bannerService.create(banner);
    }

    @PutMapping("/{id}")
    public Banner update(@PathVariable Long id, @RequestBody Banner banner) {
        return bannerService.update(id, banner);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bannerService.delete(id);
    }
}

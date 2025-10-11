package com.trustai.aggregator.controller;

import com.trustai.aggregator.entity.Slider;
import com.trustai.aggregator.repository.SlideRepository;
import com.trustai.aggregator.repository.SliderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/sliders")
@RequiredArgsConstructor
public class SliderController {
    private final SliderRepository sliderRepo;
    private final SlideRepository slideRepo;

    @GetMapping
    public List<Slider> getAll() {
        return sliderRepo.findAll();
    }

    @GetMapping("/{id}")
    public Slider getById(@PathVariable Long id) {
        return sliderRepo.findById(id).orElseThrow(() -> new RuntimeException("Slider not found"));
    }

    @PostMapping
    public Slider create(@RequestBody Slider slider) {
        slider.getSlides().forEach(s -> s.setSlider(slider));
        return sliderRepo.save(slider);
    }

    @PutMapping("/{id}")
    public Slider update(@PathVariable Long id, @RequestBody Slider updated) {
        Slider existing = sliderRepo.findById(id).orElseThrow(() -> new RuntimeException("Slider not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.getSlides().clear();
        updated.getSlides().forEach(s -> {
            s.setSlider(existing);
            existing.getSlides().add(s);
        });
        return sliderRepo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sliderRepo.deleteById(id);
    }
}

package com.trustai.common.lifecycle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReloadManager {
    private final List<Reloadable> reloadables;

    public void reloadAll() {
        reloadables.forEach(Reloadable::reload);
    }
}

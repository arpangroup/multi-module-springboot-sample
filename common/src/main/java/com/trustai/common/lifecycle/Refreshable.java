package com.trustai.common.lifecycle;

/** Reload or refresh state without full re-init */
public interface Refreshable {
    void refresh();
}

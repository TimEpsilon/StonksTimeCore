package com.github.timepsilon.randomevent.speedup;

import com.github.timepsilon.randomevent.AbstractRandomStonksEvent;

public class SRESpeedUp extends AbstractRandomStonksEvent {

    public SRESpeedUp(float weight, boolean isPositive, String combination, String description) {
        super(weight, isPositive, combination, description);
    }

    @Override
    public void start() {

    }
}

package com.github.timepsilon.randomevent.slowdown;

import com.github.timepsilon.randomevent.AbstractRandomStonksEvent;

public class SRESlowDown extends AbstractRandomStonksEvent {

    public SRESlowDown(float weight, boolean isPositive, String combination, String description) {
        super(weight, isPositive, combination, description);
    }


    @Override
    public void start() {

    }
}

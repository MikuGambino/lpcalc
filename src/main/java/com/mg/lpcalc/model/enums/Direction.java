package com.mg.lpcalc.model.enums;

public enum Direction {
    MAX("Max"), MIN("Min");

    private final String title;
    Direction(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

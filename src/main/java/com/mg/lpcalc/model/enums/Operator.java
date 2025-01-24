package com.mg.lpcalc.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum Operator {
    LEQ("<="), GEQ(">=");

    private final String title;
    Operator(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

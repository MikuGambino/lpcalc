package com.mg.lpcalc.model.enums;

public enum Operator {
    LEQ("<="), GEQ(">="), EQ("=");

    private final String title;
    Operator(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Operator getInverted() {
        if (this.equals(EQ)) return EQ;
        if (this.equals(LEQ)) return GEQ;
        return LEQ;
    }
}

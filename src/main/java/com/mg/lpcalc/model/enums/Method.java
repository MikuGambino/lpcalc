package com.mg.lpcalc.model.enums;

public enum Method {
    BASIC("Базовый симплекс-метод"), BIG_M("Метод искуственного базиса");

    private final String title;
    Method(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

package com.mg.lpcalc.model;

import lombok.Data;

@Data
public class Fraction {
    private int numerator;
    private int denominator;

    // Конструкторы
    public Fraction() {
        this.numerator = 0;
        this.denominator = 1;
    }

    public Fraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Знаменатель не может быть нулем");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public Double doubleValue() {
        return (double) (numerator / denominator);
    }
}

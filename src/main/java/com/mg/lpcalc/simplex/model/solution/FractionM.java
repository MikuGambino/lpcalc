package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.Fraction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FractionM {
    private Fraction core;
    private Fraction m;

    public FractionM(Fraction fraction) {
        this.core = fraction;
        this.m = Fraction.ZERO;
    }

    public boolean isMoreOrEqualZero() {
        if (!core.equals(Fraction.ZERO)) {
            return core.isMoreOrEqualZero();
        } else {
            return m.isMoreOrEqualZero();
        }
    }
}

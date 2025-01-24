package com.mg.lpcalc.graphical.model;

import com.mg.lpcalc.model.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Constraint {
    private List<Double> coefficients;
    private Operator operator;
    private Double rhs;

    public Double getA() {
        return coefficients.get(0);
    }

    public Double getB() {
        return coefficients.get(1);
    }

    public Double getC() {
        return rhs;
    }
}

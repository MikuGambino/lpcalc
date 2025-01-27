package com.mg.lpcalc.graphical.model;

import com.mg.lpcalc.model.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Constraint {
    private Double a;
    private Double b;
    private Double c;
    private Operator operator;
    private boolean isUnbounded;

    public boolean isLEQ() {
        return operator.equals(Operator.LEQ);
    }
}

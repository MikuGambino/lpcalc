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
    private boolean isInitial;
    private int number = 0;

    public Constraint(Double a, Double b, Double c, Operator operator, boolean isUnbounded, boolean isInitial) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.operator = operator;
        this.isUnbounded = isUnbounded;
        this.isInitial = isInitial;
    }

    public Constraint(Double a, Double b, Double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Constraint(Double a, Double b, Double c, Operator operator) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.operator = operator;
    }

    public boolean isLEQ() {
        return operator.equals(Operator.LEQ);
    }
}

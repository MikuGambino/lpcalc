package com.mg.lpcalc.simplex.model;

import com.mg.lpcalc.model.ConstraintDTO;
import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Constraint {
    private List<Fraction> coefficients;
    private Operator operator;
    private Fraction rhs;

    public Constraint(ConstraintDTO constraintDTO) {
        this.coefficients = constraintDTO.getCoefficients();
        this.operator = constraintDTO.getOperator();
        this.rhs = constraintDTO.getRhs();
    }

    public void switchOperator() {
        if (operator.equals(Operator.LEQ)) {
            this.operator = Operator.GEQ;
        } else if (operator.equals(Operator.GEQ)) {
            this.operator = Operator.LEQ;
        }
    }

    public void addCoefficient(Fraction fraction) {
        coefficients.add(fraction);
    }
}

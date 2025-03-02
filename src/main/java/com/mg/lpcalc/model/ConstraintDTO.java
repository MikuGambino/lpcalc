package com.mg.lpcalc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mg.lpcalc.model.enums.Operator;
import lombok.Data;

import java.util.List;

@Data
public class ConstraintDTO {
    @JsonProperty("coefficients")
    private List<Fraction> coefficients;
    @JsonProperty("operator")
    private Operator operator;
    @JsonProperty("rhs")
    private Fraction rhs;

    @Override
    public String toString() {
        return "Constraint{" +
                "coefficients=" + coefficients +
                ", operator='" + operator.getTitle() + '\'' +
                ", rhs=" + rhs +
                '}';
    }
}

package com.mg.lpcalc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mg.lpcalc.model.enums.Operator;
import lombok.Data;

import java.util.List;

@Data
public class Constraint {
    @JsonProperty("coefficients")
    private List<Double> coefficients;
    @JsonProperty("operator")
    private Operator operator;
    @JsonProperty("rhs")
    private Double rhs;

    @Override
    public String toString() {
        return "Constraint{" +
                "coefficients=" + coefficients +
                ", operator='" + operator.getTitle() + '\'' +
                ", rhs=" + rhs +
                '}';
    }
}

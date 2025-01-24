package com.mg.lpcalc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OptimizationProblem {
    @JsonProperty("constraints")
    private List<Constraint> constraints;
    @JsonProperty("objective")
    private ObjectiveFunc objectiveFunc;

    @Override
    public String toString() {
        return "OptimizationProblem{" +
                "constraints=" + constraints +
                ", objectiveFunc=" + objectiveFunc +
                '}';
    }
}

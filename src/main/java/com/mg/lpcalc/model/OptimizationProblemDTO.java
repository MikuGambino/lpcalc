package com.mg.lpcalc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OptimizationProblemDTO {
    @JsonProperty("constraints")
    private List<ConstraintDTO> constraints;
    @JsonProperty("objective")
    private ObjectiveFuncDTO objectiveFunc;

    @Override
    public String toString() {
        return "OptimizationProblem{" +
                "constraints=" + constraints +
                ", objectiveFunc=" + objectiveFunc +
                '}';
    }
}

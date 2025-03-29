package com.mg.lpcalc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mg.lpcalc.model.enums.Method;
import lombok.Data;

import java.util.List;

@Data
public class OptimizationProblemDTO {
    @JsonProperty("constraints")
    private List<ConstraintDTO> constraints;
    @JsonProperty("objective")
    private ObjectiveFuncDTO objectiveFunc;
    @JsonProperty("method")
    private Method method;

    public OptimizationProblemDTO(List<ConstraintDTO> constraints, ObjectiveFuncDTO objectiveFunc, Method method) {
        this.constraints = constraints;
        this.objectiveFunc = objectiveFunc;
        this.method = method;
    }
}

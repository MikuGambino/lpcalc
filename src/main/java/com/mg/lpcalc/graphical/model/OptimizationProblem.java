package com.mg.lpcalc.graphical.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mg.lpcalc.model.ConstraintDTO;
import com.mg.lpcalc.model.ObjectiveFuncDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class OptimizationProblem {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;
}

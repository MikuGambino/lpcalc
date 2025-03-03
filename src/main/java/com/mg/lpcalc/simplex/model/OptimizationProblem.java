package com.mg.lpcalc.simplex.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OptimizationProblem {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;
}

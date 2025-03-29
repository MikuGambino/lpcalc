package com.mg.lpcalc.simplex.model;

import com.mg.lpcalc.model.enums.Method;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OptimizationProblem {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;
    private Method method;
}

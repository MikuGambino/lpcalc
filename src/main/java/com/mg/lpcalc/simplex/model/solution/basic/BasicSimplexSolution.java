package com.mg.lpcalc.simplex.model.solution.basic;

import com.mg.lpcalc.simplex.model.solution.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BasicSimplexSolution implements SimplexSolution {
    private Answer answer;
    private ConstraintTransformStep convertToLessOrEqualStep;
    private ConstraintToEqualityStep constraintToEqualityStep = new ConstraintToEqualityStep();
    private FindBasisStep findBasisStep;
    private List<RemoveNegativeBStep> removeNegativeBSteps = new ArrayList<>();
    private CalculateDeltasStep calculateDeltasStep;
    private SimplexTableDTO simplexTableWithDeltas;
    private OptimalityCheckStep optimalityCheckStep;
    private List<PivotStep> pivotSteps = new ArrayList<>();
}

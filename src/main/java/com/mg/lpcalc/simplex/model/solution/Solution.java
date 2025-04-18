package com.mg.lpcalc.simplex.model.solution;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Solution {
    private Answer answer;
    private ConstraintTransformStep convertToLessOrEqualStep;
    private ConstraintToEqualityStep constraintToEqualityStep;
    private FindBasisStep findBasisStep;
    private List<RemoveNegativeBStep> removeNegativeBSteps = new ArrayList<>();
    private CalculateDeltasStep calculateDeltasStep;
    private SimplexTableDTO simplexTableWithDeltas;
    private OptimalityCheckStep optimalityCheckStep;
    private List<PivotStep> pivotSteps = new ArrayList<>();
}

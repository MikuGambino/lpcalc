package com.mg.lpcalc.simplex.model.solution;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Answer {
    private ConstraintTransformStep convertToLessOrEqualStep;
    private ConstraintToEqualityStep constraintToEqualityStep;
    private FindBasisStep findBasisStep;
    private List<RemoveNegativeBStep> removeNegativeBSteps = new ArrayList<>();
}

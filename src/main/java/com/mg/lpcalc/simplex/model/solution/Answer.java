package com.mg.lpcalc.simplex.model.solution;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Answer {
    private ConstraintTransformStep convertToLessOrEqualStep;
    private ConstraintToEqualityStep constraintToEqualityStep;
    private FindBasisStep findBasisStep;
}

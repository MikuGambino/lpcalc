package com.mg.lpcalc.simplex.model;

import com.mg.lpcalc.simplex.model.solution.ConstraintToEqualityStep;
import com.mg.lpcalc.simplex.model.solution.ConstraintTransformStep;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Answer {
    private ConstraintTransformStep convertToLessOrEqualStep;
    private ConstraintToEqualityStep constraintToEqualityStep;
}

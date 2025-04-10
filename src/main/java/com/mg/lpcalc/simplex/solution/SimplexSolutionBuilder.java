package com.mg.lpcalc.simplex.solution;

import com.mg.lpcalc.simplex.model.Answer;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.solution.ConstraintTransformStep;
import lombok.Getter;

import java.util.List;

@Getter
public class SimplexSolutionBuilder {
    private Answer answer = new Answer();

    public void convertToLessOrEqual(List<Constraint> constraints, boolean constraintsIsChanged) {
        ConstraintTransformStep constraintTransformStep = new ConstraintTransformStep(constraints, constraintsIsChanged);
        answer.setConvertToLessOrEqualStep(constraintTransformStep);
    }
}

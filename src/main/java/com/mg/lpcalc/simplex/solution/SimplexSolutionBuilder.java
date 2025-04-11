package com.mg.lpcalc.simplex.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Answer;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.solution.ConstraintToEqualityStep;
import com.mg.lpcalc.simplex.model.solution.ConstraintTransformStep;
import lombok.Getter;
import org.apache.tomcat.util.bcel.Const;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SimplexSolutionBuilder {
    private Answer answer = new Answer();
    private List<Constraint> constraints;
    private List<Integer> slackVariablesIndexes = new ArrayList<>();
    private List<Integer> constraintsIndexes = new ArrayList<>();

    public SimplexSolutionBuilder(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public void convertToLessOrEqual(List<Constraint> constraints, boolean constraintsIsChanged) {
        ConstraintTransformStep constraintTransformStep = new ConstraintTransformStep(constraints, constraintsIsChanged);
        answer.setConvertToLessOrEqualStep(constraintTransformStep);
    }

    public void addSlackVariable(int slackIndex, int constraintIndex) {
        slackVariablesIndexes.add(slackIndex);
        constraintsIndexes.add(constraintIndex);
    }

    public void tableInitComplete() {
        ConstraintToEqualityStep step = new ConstraintToEqualityStep(constraints, slackVariablesIndexes, constraintsIndexes);
        answer.setConstraintToEqualityStep(step);
    }
}

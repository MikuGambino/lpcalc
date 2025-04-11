package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.simplex.model.Constraint;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConstraintToEqualityStep {
    private List<Constraint> constraints;
    private List<Integer> slackVariablesIndexes;
    private List<Integer> constraintsIndexes;
}

package com.mg.lpcalc.simplex.model.solution.basic;

import com.mg.lpcalc.simplex.model.Constraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConstraintToEqualityStep {
    private List<Constraint> constraints = new ArrayList<>();
    private List<Integer> slackVariablesIndexes = new ArrayList<>();
    private List<Integer> constraintsIndexes = new ArrayList<>();
}

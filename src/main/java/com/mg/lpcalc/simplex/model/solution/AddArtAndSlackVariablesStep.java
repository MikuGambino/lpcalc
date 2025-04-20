package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.simplex.model.Constraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddArtAndSlackVariablesStep {
    private List<Constraint> constraints = new ArrayList<>();
    private List<Integer> slackVariablesIndexes = new ArrayList<>();
    private List<Integer> slackConstraintIndexes = new ArrayList<>();
    private List<Fraction> slackVariables = new ArrayList<>();
    private List<Integer> artVariablesIndexes = new ArrayList<>();
    private List<Integer> artConstraintIndexes = new ArrayList<>();
}

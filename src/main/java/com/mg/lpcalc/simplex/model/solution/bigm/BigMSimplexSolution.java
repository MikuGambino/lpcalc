package com.mg.lpcalc.simplex.model.solution.bigm;

import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.solution.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BigMSimplexSolution implements SimplexSolution {
    private Answer answer;
    private ObjectiveFunc objectiveFunc;
    private AddArtAndSlackVariablesStep addArtAndSlackVariablesStep = new AddArtAndSlackVariablesStep();
    private SimplexTableDTO initialSimplexTable;
    private SimplexTableDTO simplexTableWithDeltas;
    private CalculateDeltasStep calculateDeltasStep;
    private OptimalityCheckStep optimalityCheckStep;
    private List<PivotStep> pivotSteps = new ArrayList<>();
}

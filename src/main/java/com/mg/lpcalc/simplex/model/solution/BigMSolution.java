package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BigMSolution implements Solution {
    private ObjectiveFunc objectiveFunc;
    private AddArtAndSlackVariablesStep addArtAndSlackVariablesStep = new AddArtAndSlackVariablesStep();
    private SimplexTableDTO initialSimplexTable;
    private SimplexTableDTO simplexTableWithDeltas;
    private BigMCalculateDeltasStep calculateDeltasStep;
}

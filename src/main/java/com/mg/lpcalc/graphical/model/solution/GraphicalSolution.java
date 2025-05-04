package com.mg.lpcalc.graphical.model.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphicalSolution {
    private String finalGraphSVG;
    private List<AddConstraintStep> addConstraintSteps;
    private AddObjectiveFunc addObjectiveFunc;
    private GraphicalAnswer graphicalAnswer;
}

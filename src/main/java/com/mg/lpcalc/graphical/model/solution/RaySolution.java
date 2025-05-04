package com.mg.lpcalc.graphical.model.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaySolution {
    private PointSolution pointSolution;
    private String paramAnswerSystem;
    private String constraintLatex;
    private int constraintNumber;
}

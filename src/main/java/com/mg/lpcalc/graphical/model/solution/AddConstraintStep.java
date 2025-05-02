package com.mg.lpcalc.graphical.model.solution;

import com.mg.lpcalc.graphical.model.Constraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddConstraintStep {
    private Constraint constraint;
    private boolean lineParallelToAxis;
    private String inequalityLatex;
    private String equalityLatex;
    private LineParallelToAxisStep lineParallelToAxisStep;
    private FindXStep findX1;
    private FindXStep findX2;
    private FindInequalityRegion findInequalityRegion;
    private String graph;

    public AddConstraintStep(Constraint constraint, String inequalityLatex, String equalityLatex,
                             boolean lineParallelToAxis, LineParallelToAxisStep lineParallelToAxisStep,
                             FindInequalityRegion findInequalityRegion, String graph) {
        this.constraint = constraint;
        this.findInequalityRegion = findInequalityRegion;
        this.inequalityLatex = inequalityLatex;
        this.equalityLatex = equalityLatex;
        this.lineParallelToAxis = lineParallelToAxis;
        this.lineParallelToAxisStep = lineParallelToAxisStep;
        this.graph = graph;
    }

    public AddConstraintStep(Constraint constraint, String inequalityLatex, String equalityLatex,
                             boolean lineParallelToAxis, FindXStep findX1, FindXStep findX2,
                             FindInequalityRegion findInequalityRegion, String graph) {
        this.constraint = constraint;
        this.inequalityLatex = inequalityLatex;
        this.equalityLatex = equalityLatex;
        this.lineParallelToAxis = lineParallelToAxis;
        this.findX1 = findX1;
        this.findX2 = findX2;
        this.findInequalityRegion = findInequalityRegion;
        this.graph = graph;
    }
}

package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.Fraction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PivotStep {
    private SimplexTableDTO simplexTableBefore;
    private SimplexTableDTO simplexTableAfter;
    private CalculateDeltasStep calculateDeltasStep;
    private OptimalityCheckStep optimalityCheckStep;
    private Fraction pivotElement;
    private Fraction minDelta;
    private Fraction targetQ;
    private int column;
    private int row;
    private int lastBasisVariableIndex;
    private List<Fraction> simplexRelations;
    private List<Fraction> bCoefficients;
    private List<Fraction> columnCoefficients;
}

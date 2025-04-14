package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.Fraction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveNegativeBStep {
    private int column;
    private int row;
    private int oldBasis;
    private SimplexTableDTO simplexTableBefore;
    private SimplexTableDTO simplexTableAfter;
    private Fraction maxNegativeB;
    private Fraction maxNegativeRowElement;
}

package com.mg.lpcalc.simplex.model.solution.basic;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.simplex.model.solution.SimplexTableDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoveNegativeBStep {
    private int column;
    private int row;
    private int oldBasis;
    private SimplexTableDTO simplexTableBefore;
    private SimplexTableDTO simplexTableAfter;
    private Fraction maxNegativeB;
    private Fraction maxNegativeRowElement;
    private boolean success;
}

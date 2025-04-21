package com.mg.lpcalc.simplex.model.solution.basic;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.simplex.model.solution.SimplexTableDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FindBasisSubStep {
    private BasisMethod method;
    private int column;
    private int row;
    private Fraction pivotElement;
    private SimplexTableDTO simplexTableBefore;
    private SimplexTableDTO simplexTableAfter;
}

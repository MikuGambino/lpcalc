package com.mg.lpcalc.simplex.model.solution;

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
    private SimplexTableDTO simplexTableBefore;
    private SimplexTableDTO simplexTableAfter;
}

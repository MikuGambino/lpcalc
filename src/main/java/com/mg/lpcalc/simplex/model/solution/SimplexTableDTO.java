package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.simplex.table.BasicSimplexTable;
import com.mg.lpcalc.simplex.table.BigMSimplexTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplexTableDTO {
    private Fraction[][] tableau;
    private Fraction[] costs;
    private int numConstraints;
    private int numVars;
    private int numSlack;
    private int numColumns;
    private int[] basis;
    private Fraction[] mValues;

    public SimplexTableDTO(BasicSimplexTable simplexTable) {
        this.basis = simplexTable.getBasis().clone();
        this.costs = simplexTable.costsCopy();
        this.tableau = simplexTable.tableauCopy();
        this.numVars = simplexTable.getNumVars();
        this.numSlack = simplexTable.getNumSlack();
        this.numConstraints = simplexTable.getNumConstraints();
        this.numColumns = simplexTable.getNumColumns();
    }

    public SimplexTableDTO(BigMSimplexTable simplexTable) {
        this.basis = simplexTable.getBasis().clone();
        this.costs = simplexTable.costsCopy();
        this.tableau = simplexTable.tableauCopy();
        this.numVars = simplexTable.getNumVars();
        this.numSlack = simplexTable.getNumSlack();
        this.numConstraints = simplexTable.getNumConstraints();
        this.numColumns = simplexTable.getNumColumns();
        this.mValues = simplexTable.getMValues();
    }

    public SimplexTableDTO(SimplexTableDTO simplexTable) {
        this.basis = simplexTable.getBasis().clone();
        this.costs = simplexTable.costsCopy();
        this.tableau = simplexTable.tableauCopy();
        this.numVars = simplexTable.getNumVars();
        this.numSlack = simplexTable.getNumSlack();
        this.numColumns = simplexTable.getNumColumns();
        this.numConstraints = simplexTable.getNumConstraints();
        this.mValues = simplexTable.getMValues();
    }

    public Fraction[] costsCopy() {
        return Arrays.stream(this.costs)
                .map(Fraction::new)
                .toArray(Fraction[]::new);
    }

    public Fraction[][] tableauCopy() {
        if (tableau == null) return null;

        return Arrays.stream(tableau)
                .map(row -> row == null ? null :
                        Arrays.stream(row)
                                .map(fraction -> fraction != null ? new Fraction(fraction) : null)
                                .toArray(Fraction[]::new))
                .toArray(Fraction[][]::new);
    }
}

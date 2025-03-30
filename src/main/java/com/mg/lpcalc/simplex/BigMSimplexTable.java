package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;

import java.util.Arrays;
import java.util.List;

public class BigMSimplexTable extends SimplexTable {

    public BigMSimplexTable(int numSlack, int numAux, int numVars, int numConstraints, Fraction[] costs, List<Constraint> constraints) {
        this.costs = costs;
        this.numColumns = numVars + numSlack + numAux + 1;
        this.basis = new int[numConstraints];
        // -1 -> значит базис ещё не найден
        Arrays.fill(basis, -1);
        this.numVars = numVars;
        this.numConstraints = numConstraints;
        int curSlackCount = 0;
        int curAuxCount = 0;

        // +1 строка для целевой функции, +1 столбец для правой части (RHS)
        this.tableau = new Fraction[numConstraints + 1][numVars + numSlack + numAux + 1];
        for (int i = 0; i < numConstraints; i++) {
            Constraint constraint = constraints.get(i);
            // копирование коэффициентов ограничений
            for (int j = 0; j < numVars; j++) {
                tableau[i][j] = constraint.getCoefficients().get(j);
            }

            for (int j = numVars; j < numVars + numSlack + numAux; j++) {
                tableau[i][j] = Fraction.ZERO;
            }

            // добавляем переменные балансировки
            if (constraint.getOperator().equals(Operator.LEQ) || constraint.getOperator().equals(Operator.GEQ)) {
                if (constraint.getOperator().equals(Operator.LEQ)) {
                    tableau[i][numVars + curSlackCount] = Fraction.ONE;
                } else {
                    tableau[i][numVars + curSlackCount] = Fraction.ONE.negate();
                }
                basis[i] = numVars + curSlackCount;
                curSlackCount++;
            }

            // добавляем правую часть (RHS)
            tableau[i][numVars + numSlack + numAux] = constraint.getRhs();
        }

        // добавляем искусственные переменные
        for (int i = 0; i < numConstraints; i++) {
            Constraint constraint = constraints.get(i);
            if (!constraint.getOperator().equals(Operator.LEQ)) {
                tableau[i][numVars + curSlackCount + curAuxCount] = Fraction.ONE;
                basis[i] = numVars + curSlackCount + curAuxCount;
                curAuxCount++;
            }
        }
    }
}

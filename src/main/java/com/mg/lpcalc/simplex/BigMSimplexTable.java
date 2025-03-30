package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;

import java.util.Arrays;
import java.util.List;

public class BigMSimplexTable extends SimplexTable {

    private Fraction[] mValues;
    private Direction direction;

    public BigMSimplexTable(int numSlack, int numAux, int numVars, int numConstraints, Fraction[] costs,
                            List<Constraint> constraints, Direction direction) {
        this.costs = costs;
        this.numSlack = numSlack;
        this.direction = direction;
        this.numColumns = numVars + numSlack + numAux + 1;
        this.basis = new int[numConstraints];
        this.mValues = new Fraction[numColumns];
        // заполнение массива штрафов нулями
        Arrays.fill(mValues, Fraction.ZERO);
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

    public void calculateDeltas() {
        Fraction mValue = direction.equals(Direction.MAX) ? Fraction.ONE.negate() : Fraction.ONE;
        for (int i = 0; i < numColumns; i++) {
            Fraction delta = Fraction.ZERO;
            Fraction deltaM = Fraction.ZERO;
            for (int j = 0; j < numConstraints; j++) {
                if (isAuxVariable(basis[j])) {
                    deltaM = deltaM.add(mValue.multiply(tableau[j][i]));
                } else {
                    delta = delta.add(costs[basis[j]].multiply(tableau[j][i])); // Δi = ce1·a1i + ce2·a2i + ... + cem·ami
                }
            }
            if (i != numColumns - 1) {
                if (isAuxVariable(i)) {
                    deltaM = deltaM.subtract(mValue);
                } else {
                    delta = delta.subtract(costs[i]);
                }
            }

            tableau[numConstraints][i] = delta;
            mValues[i] = deltaM;
        }
    }

    private boolean isAuxVariable(int variable) {
        return variable >= numSlack + numVars;
    }

    public void print() {
        for (Fraction[] fractions : tableau) {
            System.out.println(Arrays.toString(fractions));
        }

        for (int b : basis) {
            System.out.print(b + 1 + " ");
        }
        System.out.println();
        System.out.println(Arrays.toString(mValues));
    }
}

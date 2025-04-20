package com.mg.lpcalc.simplex.table;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.solution.SimplexSolutionBuilderBigM;

import java.util.Arrays;
import java.util.List;

public class BigMSimplexTable extends SimplexTable {

    private Fraction[] mValues;
    private Direction direction;
    private SimplexSolutionBuilderBigM builderBigM;

    public BigMSimplexTable(int numSlack, int numAux, int numVars, int numConstraints, Fraction[] costs,
                            List<Constraint> constraints, ObjectiveFunc objectiveFunc, SimplexSolutionBuilderBigM builder) {
        this.builderBigM = builder;
        this.solutionBuilder = builderBigM.getBuilder();
        this.costs = costs;
        this.numSlack = numSlack;
        this.direction = objectiveFunc.getDirection();
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
        int curArtVariables = 0;

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
                Fraction variable;
                if (constraint.getOperator().equals(Operator.LEQ)) {
                    variable = Fraction.ONE;
                } else {
                    variable = Fraction.ONE.negate();
                }
                tableau[i][numVars + curSlackCount] = variable;
                basis[i] = numVars + curSlackCount;
                builderBigM.addSlackVariable(numVars + curSlackCount + 1, i, variable);
                curSlackCount++;
            }

            // добавляем правую часть (RHS)
            tableau[i][numVars + numSlack + numAux] = constraint.getRhs();
        }

        // добавляем искусственные переменные
        for (int i = 0; i < numConstraints; i++) {
            Constraint constraint = constraints.get(i);
            if (!constraint.getOperator().equals(Operator.LEQ)) {
                tableau[i][numVars + curSlackCount + curArtVariables] = Fraction.ONE;
                basis[i] = numVars + curSlackCount + curArtVariables;
                builderBigM.addArtificialVariable(curArtVariables + 1, i);
                curArtVariables++;
            }
        }

        builderBigM.tableInitialized(objectiveFunc, this);
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

    public boolean isOptimal(Direction direction) {
        for (int i = 0; i < numColumns - 1; i++) {
            if (tableau[numConstraints][i].equals(Fraction.ZERO)) continue;
            if (Direction.MAX.equals(direction) && !isPositiveDelta(i)) return false;
            if (Direction.MIN.equals(direction) && isPositiveDelta(i)) return false;
        }

        return true;
    }

    public int findPivotColumn(Direction direction) {
        int columnIdx = 0;

        for (int i = 0; i < numColumns - 1; i++) {
            if (Direction.MAX.equals(direction) && (isZeroDelta(columnIdx) || !isFirstDeltaGreater(i, columnIdx)) ||
                    Direction.MIN.equals(direction) && (isZeroDelta(columnIdx) || isFirstDeltaGreater(i, columnIdx))) {
                columnIdx = i;
            }
        }

        return columnIdx;
    }

    private boolean isPositiveDelta(int column) {
        Fraction delta = tableau[numConstraints][column];
        Fraction deltaM = mValues[column];

        return deltaM.isPositive() || deltaM.equals(Fraction.ZERO) && delta.isPositive();
    }

    private boolean isZeroDelta(int column) {
        return tableau[numConstraints][column].equals(Fraction.ZERO) && mValues[column].equals(Fraction.ZERO);
    }

    private boolean isFirstDeltaGreater(int first, int second) {
        Fraction firstDelta = tableau[numConstraints][first];
        Fraction firstDeltaM = mValues[first];

        Fraction secondDelta = tableau[numConstraints][second];
        Fraction secondDeltaM = mValues[second];

        if (firstDeltaM.equals(Fraction.ZERO) && secondDeltaM.equals(Fraction.ZERO)) {
            return firstDelta.isGreater(secondDelta);
        }

        return firstDeltaM.isGreater(secondDeltaM);
    }

    public boolean solutionContainsArtVariables() {
        for (int i = 0; i < numSlack + numVars; i++) {
            if (!mValues[i].equals(Fraction.ZERO)) return true;
        }

        return false;
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

    public boolean pivot(Direction direction, SimplexSolutionBuilderBigM solutionBuilder) {
        int pivotColumn = findPivotColumn(direction);
        int pivotRow = findPivotRow(pivotColumn);
        if (pivotRow == -1) {
            System.out.println("Целевая функция не ограничена и решения не существует");
            return false;
        }

        gaussianElimination(pivotRow, pivotColumn);
        setBasisVariable(pivotColumn, pivotRow);
        return true;
    }
}

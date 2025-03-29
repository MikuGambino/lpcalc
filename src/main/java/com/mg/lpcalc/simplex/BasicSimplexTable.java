package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;

import java.util.Arrays;
import java.util.List;

public class BasicSimplexTable extends SimplexTable {

    public BasicSimplexTable(int numSlack, int numVars, int numConstraints, Fraction[] costs, List<Constraint> constraints) {
        this.costs = costs;
        this.numColumns = numVars + numSlack + 1;
        this.basis = new int[numConstraints];
        // -1 -> значит базис ещё не найден
        Arrays.fill(basis, -1);
        this.numVars = numVars;
        this.numConstraints = numConstraints;
        int curSlackCount = 0;

        // +1 строка для целевой функции, +1 столбец для правой части (RHS)
        this.tableau = new Fraction[numConstraints + 1][numVars + numSlack + 1];

        for (int i = 0; i < numConstraints; i++) {
            Constraint constraint = constraints.get(i);
            // копирование коэффициентов ограничений
            for (int j = 0; j < numVars; j++) {
                tableau[i][j] = constraint.getCoefficients().get(j);
            }

            for (int j = numVars; j < numVars + numSlack; j++) {
                tableau[i][j] = Fraction.ZERO;
            }

            // добавляем переменные балансировки
            if (!constraint.getOperator().equals(Operator.EQ)) {
                tableau[i][numVars + curSlackCount] = Fraction.ONE;
                basis[i] = numVars + curSlackCount;
                curSlackCount++;
            }

            // добавляем правую часть (RHS)
            tableau[i][numVars + numSlack] = constraint.getRhs();
        }

        // Начальное значение целевой функции = 0
        tableau[numConstraints][numVars + numSlack] = Fraction.ZERO;
        print();
    }

    public int findPivotColumn(Direction direction) {
        int columnIdx = 0;
        Fraction targetDelta = tableau[numConstraints][columnIdx];

        for (int i = 0; i < numColumns; i++) {
            Fraction currentDelta = tableau[numConstraints][i];
            if (Direction.MAX.equals(direction) && (targetDelta.equals(Fraction.ZERO) || currentDelta.isLess(targetDelta)) ||
                    Direction.MIN.equals(direction) && (targetDelta.equals(Fraction.ZERO) || currentDelta.isGreater(targetDelta))) {
                targetDelta = currentDelta;
                columnIdx = i;
            }
        }

        return columnIdx;
    }

    public boolean isOptimal(Direction direction) {
        for (int i = 0; i < numColumns; i++) {
            Fraction delta = tableau[numConstraints][i];
            if (Direction.MAX.equals(direction) && delta.isNegative()) return false;
            if (Direction.MIN.equals(direction) && delta.isPositive()) return false;
        }

        return true;
    }

    public void calculateDeltas() {
        for (int i = 0; i < numColumns; i++) {
            Fraction delta = Fraction.ZERO;
            for (int j = 0; j < numConstraints; j++) {
                delta = delta.add(costs[basis[j]].multiply(tableau[j][i])); // Δi = ce1·a1i + ce2·a2i + ... + cem·ami
            }
            if (i != numColumns - 1) {
                delta = delta.subtract(costs[i]);
            }
            tableau[numConstraints][i] = delta;
        }
    }

    public boolean pivot(Direction direction) {
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

    public void checkUnboundedDirection(Direction direction, ObjectiveFunc objectiveFunc) {
        int pivotColumn = findPivotColumn(direction);
        if (objectiveFunc.getCoefficients().get(pivotColumn).isNegative()) System.out.println("Убывает");
        if (objectiveFunc.getCoefficients().get(pivotColumn).isPositive()) System.out.println("Возрастает");
    }
}

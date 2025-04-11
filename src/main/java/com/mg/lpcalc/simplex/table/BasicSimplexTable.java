package com.mg.lpcalc.simplex.table;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.solution.SimplexSolutionBuilder;

import java.util.Arrays;
import java.util.List;

public class BasicSimplexTable extends SimplexTable {

    private SimplexSolutionBuilder solutionBuilder;

    public BasicSimplexTable(int numSlack, int numVars, int numConstraints, Fraction[] costs, List<Constraint> constraints,
                             SimplexSolutionBuilder solutionBuilder) {
        this.solutionBuilder = solutionBuilder;
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
                solutionBuilder.addSlackVariable(numVars + curSlackCount + 1, i);
                curSlackCount++;
            }

            // добавляем правую часть (RHS)
            tableau[i][numVars + numSlack] = constraint.getRhs();
        }

        // Начальное значение целевой функции = 0
        tableau[numConstraints][numVars + numSlack] = Fraction.ZERO;
        print();
        solutionBuilder.tableInitComplete();
    }

    public void print() {
        for (Fraction[] fractions : tableau) {
            System.out.println(Arrays.toString(fractions));
        }

        for (int b : basis) {
            System.out.print(b + 1 + " ");
        }
        System.out.println();
    }

    public int findPivotColumn(Direction direction) {
        int columnIdx = 0;
        Fraction targetDelta = tableau[numConstraints][columnIdx];

        for (int i = 0; i < numColumns - 1; i++) {
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
        for (int i = 0; i < numColumns - 1; i++) {
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
}

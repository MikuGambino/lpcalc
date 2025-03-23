package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.RowColumnPair;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class SimplexTable {
    private Fraction[][] tableau;
    private Fraction[] costs;
    private int numConstraints;
    private int numVars;
    private int numSlack;
    private int numColumns;
    private int[] basis;

    public SimplexTable(int numSlack, int numVars, int numConstraints, Fraction[] costs, List<Constraint> constraints) {
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

    public void print() {
        for (Fraction[] fractions : tableau) {
            System.out.println(Arrays.toString(fractions));
        }

        System.out.println(Arrays.toString(basis));
    }

    // Возвращает индекс Fraction.ONE если единичный вектор существует
    public RowColumnPair findNonBasisUnitVector() {
        for (int i = 0; i < numVars + numSlack; i++) {
            boolean isUnitVector = true;
            boolean isUnitFractionFound = false;
            int constraintIndex = 0;
            for (int j = 0; j < numConstraints; j++) {
                boolean fractionIsUnit = tableau[j][i].equals(Fraction.ONE);
                boolean fractionIsZero = tableau[j][i].equals(Fraction.ZERO);
                if (fractionIsUnit && isUnitFractionFound) {
                    isUnitVector = false;
                } else if (fractionIsUnit && !isUnitFractionFound) {
                    constraintIndex = j;
                    isUnitFractionFound = true;
                } else if (!fractionIsZero) {
                    isUnitVector = false;
                }
            }

            if (isUnitVector && basis[constraintIndex] == -1) {
                return new RowColumnPair(constraintIndex, i);
            }
        }

        return null;
    }

    // Возвращает индекс ненулевое число если вектор с единственным ненулевым числом существует
    public RowColumnPair findNonBasisColumnWithSingleNonZeroElement() {
        for (int i = 0; i < numVars + numSlack; i++) {
            boolean isCorrectVector = true;
            boolean nonZeroFound = false;
            int constraintIndex = 0;
            for (int j = 0; j < numConstraints; j++) {
                boolean fractionIsNonZero = !tableau[j][i].equals(Fraction.ZERO);
                if (fractionIsNonZero && nonZeroFound) {
                    isCorrectVector = false;
                } else if (fractionIsNonZero) {
                    nonZeroFound = true;
                    constraintIndex = j;
                }
            }

            if (isCorrectVector && basis[constraintIndex] == -1) {
                return new RowColumnPair(constraintIndex, i);
            }
        }

        return null;
    }

    public void divideRow(int row, Fraction divisor) {
        for (int i = 0; i < numColumns; i++) {
            tableau[row][i] = tableau[row][i].divide(divisor);
        }
    }

    public void subtractRow(int targetRowIndex, int sourceRowIndex, Fraction multiplier) {
        for (int i = 0; i < numColumns; i++) {
            Fraction sourceElement = tableau[sourceRowIndex][i];
            Fraction reduced = tableau[targetRowIndex][i];
            Fraction deductible = sourceElement.multiply(multiplier);
            tableau[targetRowIndex][i] = reduced.subtract(deductible);
        }
    }

    public int findFirstNonBasisColumn() {
        for (int i = 0; i < numVars + numSlack; i++) {
            boolean basisFound = false;
            for (int j = 0; j < basis.length; j++) {
                if (basis[j] == i) {
                    basisFound = true;
                    break;
                }
            }

            if (isNullableColumn(i)) continue;
            if (!basisFound) return i;
        }

        return -1;
    }

    public boolean isNullableColumn(int column) {
        for (int i = 0; i < numConstraints; i++) {
            if (!tableau[i][column].equals(Fraction.ZERO)) {
                return false;
            }
        }

        return true;
    }

    public Fraction getElement(int row, int column) {
        checkIndexes(row, column);
        return tableau[row][column];
    }

    public void gaussianElimination(int row, int column) {
        divideRow(row, tableau[row][column]);

        print();

        for (int i = 0; i < numConstraints; i++) {
            if (i == row) continue;
            subtractRow(i, row, tableau[i][column]);
            print();
        }
    }

    public boolean isContainsNegativeB() {
        for (int i = 0; i < numConstraints; i++) {
            if (this.tableau[i][numColumns - 1].isNegative()) {
                return true;
            }
        }

        return false;
    }

    // Поиск строки, в которой отрицательный свободный коэффициент максимален по модулю
    public int findMaxModuloNegativeBRow() {
        int row = 0;
        Fraction maxAbsB = Fraction.ZERO;
        for (int i = 0; i < numConstraints; i++) {
            Fraction currentFraction = this.tableau[i][numColumns - 1];
            if (!currentFraction.isNegative()) continue;
            if (currentFraction.abs().isGreater(maxAbsB.abs())) {
                row = i;
                maxAbsB = currentFraction;
            }
        }

        return row;
    }

    public int finMaxModuloNegativeColumn(int row) {
        int column = -1;
        Fraction maxAbsFraction = Fraction.ZERO;
        for (int i = 0; i < numVars + numSlack; i++) {
            Fraction currentFraction = this.tableau[row][i];
            if (!currentFraction.isNegative()) continue;
            if (currentFraction.abs().isGreater(maxAbsFraction.abs())) {
                maxAbsFraction = currentFraction;
                column = i;
            }
        }

        return column;
    }

    // Потенциально перенести в SimplexSolver
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

    public void setBasisVariable(int variable, int position) {
        this.basis[position] = variable;
    }

    public void checkIndexes(int row, int column) {
        if (row < 0 || row >= tableau.length) {
            throw new IndexOutOfBoundsException("Индекс строки вне допустимого диапазона: " + row);
        }
        if (column < 0 || column >= numColumns) {
            throw new IndexOutOfBoundsException("Индекс столбца вне допустимого диапазона: " + column);
        }
    }
}

package com.mg.lpcalc.simplex.table;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.RowColumnPair;
import com.mg.lpcalc.simplex.model.solution.Answer;
import com.mg.lpcalc.simplex.model.solution.AnswerType;
import com.mg.lpcalc.simplex.model.solution.SimplexTableDTO;
import com.mg.lpcalc.simplex.solution.SimplexSolutionBuilder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// todo возможно добавить абстрактных методов
@Data
public abstract class SimplexTable {
    protected Fraction[][] tableau;
    protected Fraction[] costs;
    protected int numConstraints;
    protected int numVars;
    protected int numSlack;
    protected int numColumns;
    protected int[] basis;
    protected SimplexSolutionBuilder solutionBuilder;

    public abstract int findPivotColumn(Direction direction);

    public void checkUnboundedDirection(Direction direction, ObjectiveFunc objectiveFunc) {
        int pivotColumn = findPivotColumn(direction);
        if (objectiveFunc.getCoefficients().get(pivotColumn).isNegative()) {
            solutionBuilder.createUnsuccessfulPivotStep(pivotColumn, Direction.MIN, new SimplexTableDTO(this));
        }
        if (objectiveFunc.getCoefficients().get(pivotColumn).isPositive()) {
            solutionBuilder.createUnsuccessfulPivotStep(pivotColumn, Direction.MAX, new SimplexTableDTO(this));
        }
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

        for (int i = 0; i < numConstraints; i++) {
            if (i == row) continue;
            subtractRow(i, row, tableau[i][column]);
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


    public int findPivotRow(int column) {
        List<Fraction> simplexRatioList = new ArrayList<>();
        for (int i = 0; i < numConstraints; i++) {
            Fraction freeCoefficient = tableau[i][numColumns - 1];
            Fraction columnElement = tableau[i][column];
            if (columnElement.equals(Fraction.ZERO)) {
                simplexRatioList.add(Fraction.ZERO);
                continue;
            }
            Fraction simplexRatio = freeCoefficient.divide(columnElement);
            simplexRatioList.add(simplexRatio);
        }

        if (simplexRatioList.isEmpty()) return -1;

        int rowIdx = -1;
        Fraction minQ = Fraction.ZERO;
        for (int i = 0; i < simplexRatioList.size(); i++) {
            Fraction simplexRatio = simplexRatioList.get(i);
            if (simplexRatio.isNegative() || simplexRatio.equals(Fraction.ZERO)) continue;
            if (simplexRatio.isLess(minQ) || minQ.equals(Fraction.ZERO)) {
                minQ = simplexRatio;
                rowIdx = i;
            }
        }

        solutionBuilder.saveSimplexRelations(simplexRatioList, minQ);
        return rowIdx;
    }

    public void setBasisVariable(int variable, int position) {
        this.basis[position] = variable;
    }

    public int getVariablePosition(int num) {
        for (int i = 0; i < basis.length; i++) {
            if (basis[i] == num) {
                return i;
            }
        }

        return -1;
    }

    public Answer getFinalAnswer() {
        List<Fraction> variablesValues = new ArrayList<>();
        for (int i = 0; i < numVars; i++) {
            int variablePosition = getVariablePosition(i);
            if (variablePosition == -1) {
                variablesValues.add(Fraction.ZERO);
                continue;
            }
            variablesValues.add(tableau[variablePosition][numColumns - 1]);
        }

        Fraction objectiveValue = tableau[numConstraints][numColumns - 1];
        return new Answer(AnswerType.SUCCESS, variablesValues, objectiveValue);
    }

    public void checkIndexes(int row, int column) {
        if (row < 0 || row >= tableau.length) {
            throw new IndexOutOfBoundsException("Индекс строки вне допустимого диапазона: " + row);
        }
        if (column < 0 || column >= numColumns) {
            throw new IndexOutOfBoundsException("Индекс столбца вне допустимого диапазона: " + column);
        }
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


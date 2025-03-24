package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.OptimizationProblem;
import com.mg.lpcalc.simplex.model.RowColumnPair;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SimplexSolver {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;
    private SimplexTable currentSimplexTable;
    private Direction direction;
    private int numVars;
    private int numConstraints;
    private int numSlacks;

    public SimplexSolver(OptimizationProblem problem) {
        this.constraints = problem.getConstraints();
        this.objectiveFunc = problem.getObjectiveFunc();
        this.numVars = constraints.get(0).getCoefficients().size();
        this.numConstraints = constraints.size();
        this.numSlacks = countSlacks();
        this.direction = objectiveFunc.getDirection();
    }

    public void solve() {
        // Если есть неравенства со знаком >=, умножаем их на -1
        makeConstraintsLEQ();
        // инициализация начальной симплекс таблицы
        this.currentSimplexTable = new SimplexTable(
                numSlacks,
                numVars,
                numConstraints,
                getCosts(),
                constraints
        );
        findInitialBasis();
        if (currentSimplexTable.isContainsNegativeB()) {
            removeNegativeB();
        }
        if (currentSimplexTable.isContainsNegativeB()) {
            System.out.println("Нет решения");
            return;
        }
        this.currentSimplexTable.calculateDeltas();
        this.currentSimplexTable.print();
        System.out.println("Is optimal " + this.currentSimplexTable.isOptimal(objectiveFunc.getDirection()));
        while (!this.currentSimplexTable.isOptimal(direction)) {
            this.currentSimplexTable.pivot(direction);
            this.currentSimplexTable.calculateDeltas();
            this.currentSimplexTable.print();
        }
    }

    private void removeNegativeB() {
        while (currentSimplexTable.isContainsNegativeB()) {
            this.currentSimplexTable.print();
            int row = this.currentSimplexTable.findMaxModuloNegativeBRow();
            int column = this.currentSimplexTable.finMaxModuloNegativeColumn(row);
            if (column == -1) {
                break;
            }
            this.currentSimplexTable.setBasisVariable(column, row);
            this.currentSimplexTable.gaussianElimination(row, column);
            this.currentSimplexTable.print();
        }
    }

    private void makeConstraintsLEQ() {
        for (Constraint constraint : constraints) {
            if (constraint.getOperator().equals(Operator.GEQ)) {
                List<Fraction> newCoefficients = new ArrayList<>();
                for (Fraction coefficient : constraint.getCoefficients()) {
                    newCoefficients.add(coefficient.negate());
                }
                constraint.setRhs(constraint.getRhs().negate());
                constraint.setCoefficients(newCoefficients);
                constraint.setOperator(Operator.LEQ);
            }
        }
    }

    // допустимость начального базисного решения
    private boolean isInitialSolutionFeasible() {
        for (Constraint constraint : constraints) {
            if (constraint.getRhs().isNegative()) {
                return false;
            }
        }

        return true;
    }

    // подсчёт количества неравенств
    private int countSlacks() {
        int num = 0;
        for (Constraint constraint : constraints) {
            if (!constraint.getOperator().equals(Operator.EQ)) {
                num++;
            }
        }

        return num;
    }

    private Fraction[] getCosts() {
        Fraction[] costs = new Fraction[numVars + numSlacks];
        for (int i = 0; i < numVars; i++) {
            costs[i] = objectiveFunc.getCoefficients().get(i);
        }
        for (int i = numVars; i < costs.length; i++) {
            costs[i] = Fraction.ZERO;
        }

        return costs;
    }

    private void findInitialBasis() {
        int[] basis = currentSimplexTable.getBasis();
        for (int i = 0; i < basis.length; i++) {
            if (basis[i] != -1) {
                continue;
            }
            if (tryFindBasisUsingUnitVectors()) {
                continue;
            }
            if (tryFindBasisUsingSingleNonZeroElements()) {
                continue;
            }
            createBasisUsingGaussianElimination(i);
        }

        currentSimplexTable.print();
    }

    private boolean tryFindBasisUsingUnitVectors() {
        RowColumnPair rowColumnPair = currentSimplexTable.findNonBasisUnitVector();
        if (rowColumnPair == null) {
            return false;
        }

        int[] basis = currentSimplexTable.getBasis();
        basis[rowColumnPair.getRow()] = rowColumnPair.getColumn();

        return true;
    }

    private boolean tryFindBasisUsingSingleNonZeroElements() {
        RowColumnPair rowColumnPair = currentSimplexTable.findNonBasisColumnWithSingleNonZeroElement();
        if (rowColumnPair == null) {
            return false;
        }

        Fraction divisor = currentSimplexTable.getElement(rowColumnPair.getRow(), rowColumnPair.getColumn());
        currentSimplexTable.divideRow(rowColumnPair.getRow(), divisor);

        int[] basis = currentSimplexTable.getBasis();
        basis[rowColumnPair.getRow()] = rowColumnPair.getColumn();

        return true;
    }

    private void createBasisUsingGaussianElimination(int row) {
        int column = currentSimplexTable.findFirstNonBasisColumn();
        currentSimplexTable.gaussianElimination(row, column);

        int[] basis = currentSimplexTable.getBasis();
        basis[row] = column;
    }
}

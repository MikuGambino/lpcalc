package com.mg.lpcalc.simplex.solver;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Answer;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.RowColumnPair;
import com.mg.lpcalc.simplex.solution.SimplexSolutionBuilder;
import com.mg.lpcalc.simplex.table.BasicSimplexTable;

import java.util.ArrayList;
import java.util.List;

public class BasicSimplexMethod implements SimplexMethod {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;
    private BasicSimplexTable simplexTable;
    private Direction direction;
    private int numVars;
    private int numConstraints;
    private int numSlacks;
    private SimplexSolutionBuilder solutionBuilder;

    public BasicSimplexMethod(List<Constraint> constraints, ObjectiveFunc objectiveFunc, Direction direction,
                              int numVars, int numConstraints, SimplexSolutionBuilder solutionBuilder) {
        this.constraints = constraints;
        this.objectiveFunc = objectiveFunc;
        this.direction = direction;
        this.numVars = numVars;
        this.numConstraints = numConstraints;
        this.numSlacks = countSlacks();
        this.solutionBuilder = solutionBuilder;
    }

    public Answer run() {
        // если есть неравенства с >=, умножаем уравнение на -1
        makeConstraintsLEQ();

        this.simplexTable = new BasicSimplexTable(
                countSlacks(),
                numVars,
                numConstraints,
                getCosts(),
                constraints
        );

        findInitialBasis();

        if (simplexTable.isContainsNegativeB()) {
            removeNegativeB();
        }

        if (simplexTable.isContainsNegativeB()) {
            System.out.println("Нет решения");
            return null;
        }

        simplexTable.calculateDeltas();

        System.out.println("Is optimal " + simplexTable.isOptimal(direction));

        while (!simplexTable.isOptimal(direction)) {
            boolean success = simplexTable.pivot(direction);
            if (!success) {
                simplexTable.checkUnboundedDirection(direction, objectiveFunc);
                return null;
            }
            simplexTable.calculateDeltas();
            simplexTable.print();
        }

        Answer answer = simplexTable.getFinalAnswer();
        return answer;
    }

    private void removeNegativeB() {
        while (simplexTable.isContainsNegativeB()) {
            this.simplexTable.print();
            int row = this.simplexTable.findMaxModuloNegativeBRow();
            int column = this.simplexTable.finMaxModuloNegativeColumn(row);
            if (column == -1) {
                break;
            }
            this.simplexTable.setBasisVariable(column, row);
            this.simplexTable.gaussianElimination(row, column);
            this.simplexTable.print();
        }
    }

    public Fraction[] getCosts() {
        Fraction[] costs = new Fraction[numVars + numSlacks];
        for (int i = 0; i < numVars; i++) {
            costs[i] = objectiveFunc.getCoefficients().get(i);
        }
        for (int i = numVars; i < costs.length; i++) {
            costs[i] = Fraction.ZERO;
        }

        return costs;
    }

    // подсчёт количества неравенств
    public int countSlacks() {
        int num = 0;
        for (Constraint constraint : constraints) {
            if (!constraint.getOperator().equals(Operator.EQ)) {
                num++;
            }
        }

        return num;
    }

    private void findInitialBasis() {
        int[] basis = simplexTable.getBasis();
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

        simplexTable.print();
    }

    private boolean tryFindBasisUsingUnitVectors() {
        RowColumnPair rowColumnPair = simplexTable.findNonBasisUnitVector();
        if (rowColumnPair == null) {
            return false;
        }

        int[] basis = simplexTable.getBasis();
        basis[rowColumnPair.getRow()] = rowColumnPair.getColumn();

        return true;
    }

    private boolean tryFindBasisUsingSingleNonZeroElements() {
        RowColumnPair rowColumnPair = simplexTable.findNonBasisColumnWithSingleNonZeroElement();
        if (rowColumnPair == null) {
            return false;
        }

        Fraction divisor = simplexTable.getElement(rowColumnPair.getRow(), rowColumnPair.getColumn());
        simplexTable.divideRow(rowColumnPair.getRow(), divisor);

        int[] basis = simplexTable.getBasis();
        basis[rowColumnPair.getRow()] = rowColumnPair.getColumn();

        return true;
    }

    private void createBasisUsingGaussianElimination(int row) {
        int column = simplexTable.findFirstNonBasisColumn();
        simplexTable.gaussianElimination(row, column);

        int[] basis = simplexTable.getBasis();
        basis[row] = column;
    }

    private void makeConstraintsLEQ() {
        boolean constraintsIsChanged = false;
        for (Constraint constraint : constraints) {
            if (constraint.getOperator().equals(Operator.GEQ)) {
                List<Fraction> newCoefficients = new ArrayList<>();
                for (Fraction coefficient : constraint.getCoefficients()) {
                    newCoefficients.add(coefficient.negate());
                }
                constraint.setRhs(constraint.getRhs().negate());
                constraint.setCoefficients(newCoefficients);
                constraint.switchOperator();
                constraintsIsChanged = true;
            }
        }
        solutionBuilder.convertToLessOrEqual(constraints, constraintsIsChanged);
    }
}

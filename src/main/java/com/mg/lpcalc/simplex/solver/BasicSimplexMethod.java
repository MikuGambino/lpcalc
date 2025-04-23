package com.mg.lpcalc.simplex.solver;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.solution.*;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.RowColumnPair;
import com.mg.lpcalc.simplex.model.solution.basic.BasicSimplexSimplexSolution;
import com.mg.lpcalc.simplex.model.solution.basic.BasisMethod;
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

    public SimplexSolution run() {
        // если есть неравенства с >=, умножаем уравнение на -1
        makeConstraintsLEQ();

        this.simplexTable = new BasicSimplexTable(
                countSlacks(),
                numVars,
                numConstraints,
                getCosts(),
                constraints,
                solutionBuilder
        );

        findInitialBasis();

        if (simplexTable.isContainsNegativeB()) {
            boolean success = removeNegativeB();
            if (!success) {
                return solutionBuilder.getSolution();
            }
        }

        simplexTable.calculateDeltas();
        solutionBuilder.setSimplexTableWithDeltas(new SimplexTableDTO(simplexTable));

        solutionBuilder.addOptimalityCheckStep(simplexTable.isOptimal(direction));

        while (!simplexTable.isOptimal(direction)) {
            boolean success = simplexTable.pivot(direction);
            if (!success) {
                simplexTable.checkUnboundedDirection(direction, objectiveFunc);
                solutionBuilder.addUnsuccessfulPivotStep();
                return solutionBuilder.getSolution();
            }
            simplexTable.calculateDeltas();
            solutionBuilder.addPivotStepToAnswer(new SimplexTableDTO(simplexTable), simplexTable.isOptimal(direction));
        }

        Answer answer = simplexTable.getFinalAnswer();
        BasicSimplexSimplexSolution basicSimplexSolution = solutionBuilder.getSolution();
        basicSimplexSolution.setAnswer(answer);
        return basicSimplexSolution;
    }

    private boolean removeNegativeB() {
        while (simplexTable.isContainsNegativeB()) {
            SimplexTableDTO simplexTableBefore = new SimplexTableDTO(simplexTable);
            int row = this.simplexTable.findMaxModuloNegativeBRow();
            int column = this.simplexTable.finMaxModuloNegativeColumn(row);
            if (column == -1) {
                solutionBuilder.addUnsuccessfulNegativeBStep(row, new SimplexTableDTO(simplexTable));
                return false;
            }
            this.simplexTable.setBasisVariable(column, row);
            this.simplexTable.gaussianElimination(row, column);
            solutionBuilder.addRemoveNegativeBStep(column, row, simplexTableBefore, simplexTable);
        }

        return true;
    }

    private Fraction[] getCosts() {
        Fraction[] costs = new Fraction[numVars + numSlacks + 1];
        for (int i = 0; i < numVars; i++) {
            costs[i] = objectiveFunc.getCoefficients().get(i);
        }
        for (int i = numVars; i < costs.length; i++) {
            costs[i] = Fraction.ZERO;
        }

        return costs;
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

    private void findInitialBasis() {
        int[] basis = simplexTable.getBasis();
        solutionBuilder.setSlackBasis(basis);
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

        solutionBuilder.basisFound();
    }

    private boolean tryFindBasisUsingUnitVectors() {
        SimplexTableDTO simplexTableBefore = new SimplexTableDTO(simplexTable);
        RowColumnPair rowColumnPair = simplexTable.findNonBasisUnitVector();
        if (rowColumnPair == null) {
            return false;
        }

        int[] basis = simplexTable.getBasis();
        basis[rowColumnPair.getRow()] = rowColumnPair.getColumn();

        solutionBuilder.addFindBasisSubStep(BasisMethod.UNIT_COLUMN, rowColumnPair.getColumn(), rowColumnPair.getRow(), simplexTableBefore, simplexTable);
        return true;
    }

    private boolean tryFindBasisUsingSingleNonZeroElements() {
        SimplexTableDTO simplexTableBefore = new SimplexTableDTO(simplexTable);
        RowColumnPair rowColumnPair = simplexTable.findNonBasisColumnWithSingleNonZeroElement();
        if (rowColumnPair == null) {
            return false;
        }

        Fraction divisor = simplexTable.getElement(rowColumnPair.getRow(), rowColumnPair.getColumn());
        simplexTable.divideRow(rowColumnPair.getRow(), divisor);

        int[] basis = simplexTable.getBasis();
        basis[rowColumnPair.getRow()] = rowColumnPair.getColumn();

        solutionBuilder.addFindBasisSubStep(BasisMethod.SINGLE_NONZERO, rowColumnPair.getColumn(), rowColumnPair.getRow(), simplexTableBefore, simplexTable);
        return true;
    }

    private void createBasisUsingGaussianElimination(int row) {
        SimplexTableDTO simplexTableBefore = new SimplexTableDTO(simplexTable);
        int column = simplexTable.findFirstNonBasisColumn();
        simplexTable.gaussianElimination(row, column);

        int[] basis = simplexTable.getBasis();
        basis[row] = column;
        solutionBuilder.addFindBasisSubStep(BasisMethod.GAUSS, column, row, simplexTableBefore, simplexTable);
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

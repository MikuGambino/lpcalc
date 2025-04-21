package com.mg.lpcalc.simplex.solver;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.solution.Answer;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.solution.bigm.BigMSolution;
import com.mg.lpcalc.simplex.model.solution.SimplexTableDTO;
import com.mg.lpcalc.simplex.model.solution.Solution;
import com.mg.lpcalc.simplex.solution.BigMSimplexSolutionBuilder;
import com.mg.lpcalc.simplex.table.BigMSimplexTable;

import java.util.ArrayList;
import java.util.List;

public class BigMMethod implements SimplexMethod {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;
    private BigMSimplexTable simplexTable;
    private Direction direction;
    private int numVars;
    private int numConstraints;
    private int numSlacks;
    private int numAuxVars;
    private BigMSimplexSolutionBuilder solutionBuilder;

    public BigMMethod(List<Constraint> constraints, ObjectiveFunc objectiveFunc, Direction direction,
                              int numVars, int numConstraints, BigMSimplexSolutionBuilder solutionBuilder) {
        this.constraints = constraints;
        this.objectiveFunc = objectiveFunc;
        this.direction = direction;
        this.numVars = numVars;
        this.numConstraints = numConstraints;
        this.solutionBuilder = solutionBuilder;
    }

    @Override
    public Solution run() {
        makeFreeCoefficientsPositive();
        countAuxVariables();

        this.simplexTable = new BigMSimplexTable(
                numSlacks,
                numAuxVars,
                numVars,
                numConstraints,
                getCosts(),
                constraints,
                objectiveFunc,
                solutionBuilder
        );

        simplexTable.print();
        simplexTable.calculateDeltas();
        solutionBuilder.setSimplexTableWithDeltas(new SimplexTableDTO(simplexTable));
        simplexTable.print();

        System.out.println("Is optimal: " + simplexTable.isOptimal(direction));
        solutionBuilder.addOptimalityCheckStep(simplexTable.isOptimal(direction));
        while (!simplexTable.isOptimal(direction)) {
            SimplexTableDTO simplexTableBefore = new SimplexTableDTO(simplexTable);
            boolean success = simplexTable.pivot(direction);
            if (!success) {
                simplexTable.checkUnboundedDirection(direction, objectiveFunc);
                solutionBuilder.addUnsuccessfulPivotStep(new SimplexTableDTO(simplexTable));
                return solutionBuilder.getSolution();
            }
            simplexTable.calculateDeltas();
            SimplexTableDTO simplexTableAfter = new SimplexTableDTO(simplexTable);
            solutionBuilder.addPivotStepToAnswer(simplexTableBefore, simplexTableAfter, simplexTable.isOptimal(direction));
        }

        if (simplexTable.solutionContainsArtVariables()) {
            System.out.println("Решение содержит искусственные переменные.\nРешения нет");
            solutionBuilder.answerHasArtVars();
            return solutionBuilder.getSolution();
        }

        Answer answer = simplexTable.getFinalAnswer();
        BigMSolution solution = solutionBuilder.getSolution();
        solution.setAnswer(answer);
        return solution;
    }

    private void makeFreeCoefficientsPositive() {
        for (Constraint constraint : constraints) {
            if (constraint.getRhs().isNegative()) {
                List<Fraction> newCoefficients = new ArrayList<>();
                for (Fraction coefficient : constraint.getCoefficients()) {
                    newCoefficients.add(coefficient.negate());
                }
                constraint.setRhs(constraint.getRhs().negate());
                constraint.setCoefficients(newCoefficients);
                constraint.switchOperator();
            }
        }
    }

    private void countAuxVariables() {
        for (Constraint constraint : constraints) {
            if (!constraint.getOperator().equals(Operator.LEQ)) numAuxVars++;
            if (!constraint.getOperator().equals(Operator.EQ)) numSlacks++;
        }
    }

    public Fraction[] getCosts() {
        Fraction[] costs = new Fraction[numVars + numSlacks + numAuxVars + 1];
        for (int i = 0; i < numVars; i++) {
            costs[i] = objectiveFunc.getCoefficients().get(i);
        }
        for (int i = numVars; i < costs.length; i++) {
            costs[i] = Fraction.ZERO;
        }

        return costs;
    }
}

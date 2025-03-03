package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.OptimizationProblem;

import java.util.ArrayList;
import java.util.List;

public class SimplexSolver {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;

    public SimplexSolver(OptimizationProblem problem) {
        this.constraints = problem.getConstraints();
        this.objectiveFunc = problem.getObjectiveFunc();
    }

    public void solve() {
        // Если есть неравенства со знаком >=, умножаем их на -1
        makeConstraintsLEQ();
        System.out.println(constraints);
        System.out.println(isInitialSolutionFeasible());
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
}

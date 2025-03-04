package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Operator;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.OptimizationProblem;

import java.util.ArrayList;
import java.util.Arrays;
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
        int numInequality = countInequality();
        initSimplexTable(numInequality);
        System.out.println(objectiveFunc);

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
    private int countInequality() {
        int num = 0;
        for (Constraint constraint : constraints) {
            if (!constraint.getOperator().equals(Operator.EQ)) {
                num++;
            }
        }

        return num;
    }

    private void initSimplexTable(int numSlack) {
        int numVars = constraints.get(0).getCoefficients().size();
        int numConstraints = constraints.size();

        // +1 строка для целевой функции, +1 столбец для правой части (RHS)
        Fraction[][] tableau = new Fraction[numConstraints + 1][numVars + numSlack + 1];

        for (int i = 0; i < numConstraints; i++) {
            Constraint constraint = constraints.get(i);
            // копирование коэффициентов ограничений
            for (int j = 0; j < numVars; j++) {
                tableau[i][j] = constraint.getCoefficients().get(j);
            }

            for (int j = numVars; j < numConstraints + numVars; j++) {
                tableau[i][j] = Fraction.ZERO;
            }

            // добавляем переменные балансировки
            if (!constraint.getOperator().equals(Operator.EQ)) {
                tableau[i][numVars + i] = Fraction.ONE;
            }

            // добавляем правую часть (RHS)
            tableau[i][numVars + numSlack] = constraint.getRhs();

            // Заполняем строку целевой функции (последняя строка)
            for (int j = 0; j < numVars; j++) {
                tableau[numConstraints][j] = objectiveFunc.getCoefficients().get(j);
            }

            // Остальные элементы в строке целевой функции = 0
            for (int j = numVars; j < numVars + numSlack; j++) {
                tableau[numConstraints][j] = Fraction.ZERO;
            }

            // Начальное значение целевой функции = 0
            tableau[numConstraints][numVars + numSlack] = Fraction.ZERO;
        }

        System.out.println(Arrays.deepToString(tableau));
    }
}

package com.mg.lpcalc.graphical.solution;

import com.mg.lpcalc.graphical.graph.GraphBuilder;
import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.ObjectiveFunc;
import com.mg.lpcalc.graphical.model.Point;
import com.mg.lpcalc.graphical.model.solution.*;
import com.mg.lpcalc.model.enums.Operator;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.mg.lpcalc.graphical.model.solution.LatexParser.*;

@NoArgsConstructor
public class GraphicalSolutionBuilder {
    private List<Constraint> constraints = new ArrayList<>();
    private List<List<Point>> feasibleRegions = new ArrayList<>();
    private List<List<Point>> axisPoints = new ArrayList<>();
    private GraphBuilder graphBuilder;

    public void addConstraint(Constraint constraint, List<Point> feasibleRegion, List<Point> axisPoints) {
        constraints.add(constraint);
        feasibleRegions.add(feasibleRegion);
        this.axisPoints.add(axisPoints);
    }

    public GraphicalSolution getSolution(List<Point> points, ObjectiveFunc objectiveFunc, List<Point> optimalPoints) {
        this.graphBuilder = new GraphBuilder(points);
        List<AddConstraintStep> addConstraintSteps = new ArrayList<>();

        for (int i = 0; i < constraints.size(); i++) {
            String graph = graphBuilder.addConstraint(constraints.get(i), feasibleRegions.get(i), axisPoints.get(i));
            addConstraintSteps.add(addConstraintStep(constraints.get(i), graph));
        }

        AddObjectiveFunc addObjectiveFunc = addObjectiveFunc(objectiveFunc);
        String finalGraph = graphBuilder.getFinalGraph(objectiveFunc, optimalPoints);
        return new GraphicalSolution(finalGraph, addConstraintSteps, addObjectiveFunc);
    }

    private AddConstraintStep addConstraintStep(Constraint constraint, String graph) {
        String inequalityLatex = LatexParser.parseConstraint(constraint);
        String equalityLatex = LatexParser.parseConstraint(constraint.getA(), constraint.getB(), constraint.getC(), Operator.EQ);
        FindInequalityRegion findInequalityRegion = isolateX2(constraint);
        // Если прямая параллельна оси координат
        if (constraint.getA() == 0 || constraint.getB() == 0) {
            LineParallelToAxisStep lineParallelToAxisStep = buildLineParallelToAxisStep(constraint);
            return new AddConstraintStep(constraint, inequalityLatex, equalityLatex, true, lineParallelToAxisStep, findInequalityRegion, graph);
        } else {
            FindXStep findX1Step = findX("x_1", constraint.getA(), constraint.getC()); // Выражение Х1
            FindXStep findX2Step = findX("x_2", constraint.getB(), constraint.getC()); // Выражение Х2
            return new AddConstraintStep(constraint, inequalityLatex, equalityLatex, false,
                                        findX1Step, findX2Step, findInequalityRegion, graph);
        }
    }

    private LineParallelToAxisStep buildLineParallelToAxisStep(Constraint constraint) {
        boolean isHorizontal = (constraint.getA() == 0);

        FindXStep findXStep;
        if (isHorizontal) {
            findXStep = findX("x_2", constraint.getB(), constraint.getC());
        } else {
            findXStep = findX("x_1", constraint.getA(), constraint.getC());
        }

        if (isHorizontal && constraint.getB() == 1 || !isHorizontal && constraint.getA() == 1) {
            findXStep.setInterceptSteps(new ArrayList<>());
        }

        return new LineParallelToAxisStep(isHorizontal, findXStep);
    }

    private FindXStep findX(String variableLabel, double coefficient, double constant) {
        List<String> steps = new ArrayList<>();
        double result;

        if (Math.abs(coefficient) == 1) {
            result = (coefficient == -1) ? -constant : constant;
            steps.add(String.format("$%s = %s$", variableLabel, formatNumber(result)));
        } else {
            steps.add(String.format("$%s = %s$", termToLatex(coefficient, variableLabel, false), formatNumber(constant)));
            result = constant / coefficient;
            steps.add(String.format("$%s = %s$", variableLabel, formatNumber(result)));
        }

        String formattedResult = formatNumber(result);

        return new FindXStep(steps, formattedResult);
    }

    private FindInequalityRegion isolateX2(Constraint constraint) {
        double a = constraint.getA();
        double b = constraint.getB();
        double c = constraint.getC();
        Operator operator = constraint.getOperator();

        if (a == 0 || b == 0) {
            if (b < 0 || a < 0) {
                String constraintLatex = parseConstraint(-constraint.getA(), -constraint.getB(), -constraint.getC(), operator.getInverted());
                return new FindInequalityRegion(List.of(constraintLatex), operator.getInverted());
            }
            return new FindInequalityRegion(List.of(LatexParser.parseConstraint(constraint)), operator);
        }

        List<String> steps = new ArrayList<>();
        // Переносим x1 в правую сторону
        a = -a;
        String firstStep = new StringBuilder()
                .append(termToLatex(b, "x_2", false))
                .append(operatorToLatex(operator))
                .append(termToLatex(a, "x_1", false))
                .append(numToLatex(c, true))
                .toString();

        steps.add(firstStep);

        if (b == 1) {
            return new FindInequalityRegion(steps, operator);
        }
        // Приведение коэффициента x2 к единице
        if (b < 0) {
            operator = operator.getInverted();
        }

        String secondStep = new StringBuilder()
                .append(termToLatex(1, "x_2", false))
                .append(operatorToLatex(operator))
                .append(termToLatex(a / b, "x_1", false))
                .append(numToLatex(c / b, true))
                .toString();
        steps.add(secondStep);

        return new FindInequalityRegion(steps, operator);
    }

    private AddObjectiveFunc addObjectiveFunc(ObjectiveFunc objectiveFunc) {
        String graph = graphBuilder.addObjectiveFunc(objectiveFunc);
        boolean inScale = !graphBuilder.objectiveFuncNotInScale(objectiveFunc);

        return new AddObjectiveFunc(objectiveFunc, graph, inScale);
    }
}

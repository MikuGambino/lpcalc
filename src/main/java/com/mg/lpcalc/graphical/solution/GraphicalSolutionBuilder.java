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

import static com.mg.lpcalc.graphical.solution.LatexParser.*;

@NoArgsConstructor
public class GraphicalSolutionBuilder {
    private List<Constraint> constraints = new ArrayList<>();
    private List<List<Point>> feasibleRegions = new ArrayList<>();
    private List<List<Point>> axisPoints = new ArrayList<>();
    private ObjectiveFunc objectiveFunc;
    private GraphBuilder graphBuilder;
    private GraphicalAnswer graphicalAnswer;
    private boolean feasibleRegionEmpty = false;

    public GraphicalSolutionBuilder(ObjectiveFunc objectiveFunc) {
        this.objectiveFunc = objectiveFunc;
    }

    public void addConstraint(Constraint constraint, List<Point> feasibleRegion, List<Point> axisPoints) {
        if (feasibleRegion.isEmpty() && feasibleRegionEmpty) return;
        if (feasibleRegion.isEmpty()){
            feasibleRegionEmpty = true;
        }
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

        AddObjectiveFunc addObjectiveFunc = null;
        if (!optimalPoints.isEmpty()) {
            addObjectiveFunc = addObjectiveFunc(objectiveFunc);
        }
        String finalGraph = graphBuilder.getFinalGraph(objectiveFunc, optimalPoints);
        return new GraphicalSolution(finalGraph, addConstraintSteps, addObjectiveFunc, graphicalAnswer);
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

    public PointSolution solutionOnePoint(Point point) {
        boolean pointOnAxis = pointIsOnAxis(point);
        double value = calcObjectiveValue(point);
        String objValueLatex = calculateObjectiveFuncValueLatex(objectiveFunc, point, value);
        PointSolution pointSolution;

        FindPointCoordinates findPointCoordinates;
        if (!pointOnAxis) {
            findPointCoordinates = parsePointIsIntersection(point);
        } else {
            findPointCoordinates = new FindPointCoordinates(true, point);
        }

        pointSolution = new PointSolution(point, objValueLatex, findPointCoordinates);
        String answer = parseOnePointSolutionAnswer(point, value);
        this.graphicalAnswer = new GraphicalAnswer(GraphicalSolutionType.SUCCESS_POINT, pointSolution, answer);

        return pointSolution;
    }

    private boolean pointIsOnAxis(Point point) {
        return point.getY() == 0 || point.getX() == 0;
    }

    private FindPointCoordinates parsePointIsIntersection(Point point) {
        List<Constraint> constraints = new ArrayList<>(point.getConstraints());
        Constraint constraint1 = constraints.get(0);
        Constraint constraint2 = constraints.get(1);
        if (constraint1.getNumber() > constraint2.getNumber()) {
            Constraint tmp = constraint1;
            constraint1 = constraint2;
            constraint2 = tmp;
        }
        String systemOfEquationsLatex = LatexParser.parseFindXFromSystem(List.of(constraint1, constraint2), point);
        return new FindPointCoordinates(false, systemOfEquationsLatex, point,
                constraint1.getNumber(), constraint2.getNumber());
    }

    private String calculateObjectiveFuncValueLatex(ObjectiveFunc objectiveFunc, Point point, double fValue) {
        return LatexParser.parseObjectiveFunc(objectiveFunc, point, fValue);
    }

    public void setSolutionSegment(Point point1, Point point2) {
        PointSolution pointSolution1 = solutionOnePoint(point1);
        PointSolution pointSolution2 = solutionOnePoint(point2);

        SegmentSolution segmentSolution = new SegmentSolution(pointSolution1, pointSolution2);
        String answer = LatexParser.parseSegmentAnswer(point1, point2, calcObjectiveValue(point1));
        this.graphicalAnswer = new GraphicalAnswer(GraphicalSolutionType.SUCCESS_SEGMENT, segmentSolution, answer);
    }

    public void setSolutionRay(Point point1, Point point2) {
        Point point;
        Point unboundedPoint;

        if (point1.isUnbounded()) {
            unboundedPoint = point1;
            point = point2;
        } else {
            unboundedPoint = point2;
            point = point1;
        }

        PointSolution pointSolution = solutionOnePoint(point);
        Constraint constraint = findRayConstraint(point, unboundedPoint);
        String systemLatex = parseRayAnswerSystem(point, unboundedPoint, constraint);
        String constraintLatex = LatexParser.parseConstraint(constraint);
        int constraintNumber = constraint.getNumber();
        RaySolution raySolution = new RaySolution(pointSolution, systemLatex, constraintLatex, constraintNumber);
        this.graphicalAnswer = new GraphicalAnswer(GraphicalSolutionType.SUCCESS_RAY, raySolution, systemLatex);
    }

    private Constraint findRayConstraint(Point point, Point unboundedPoint) {
        List<Constraint> pointConstraints = new ArrayList<>(point.getConstraints());
        List<Constraint> unboundedPointConstraints = new ArrayList<>(unboundedPoint.getConstraints());

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (pointConstraints.get(i).getNumber() == unboundedPointConstraints.get(j).getNumber()) {
                    return pointConstraints.get(i);
                }
            }
        }

        return null;
    }

    private String parseRayAnswerSystem(Point point, Point unboundedPoint, Constraint constraint) {
        StringBuilder systemLatex;
        if (unboundedPoint.getY() > point.getY()) {
            systemLatex = parseParamSystemOfEquation(point, -constraint.getB(), constraint.getA(), "", "t");
        } else {
            systemLatex = parseParamSystemOfEquation(point, constraint.getB(), -constraint.getA(), "", "t");
        }

        double fValue = calcObjectiveValue(point);
        System.out.println(LatexParser.addTConstraintRay(systemLatex, fValue));
        return LatexParser.addTConstraintRay(systemLatex, fValue);
    }

    public void setUnboundedFunction() {
        if (objectiveFunc.isMinimization()) {
            this.graphicalAnswer = new GraphicalAnswer(GraphicalSolutionType.UNBOUNDED_MIN);
            graphicalAnswer.setAnswer("Функция неограниченно убывает.");
        } else {
            this.graphicalAnswer = new GraphicalAnswer(GraphicalSolutionType.UNBOUNDED_MAX);
            graphicalAnswer.setAnswer("Функция неограниченно возрастает.");
        }
    }

    public void setFeasibleRegionPoint(Point point) {
        solutionOnePoint(point);
        this.graphicalAnswer.setGraphicalSolutionType(GraphicalSolutionType.FEASIBLE_REGION_POINT);
    }

    public void setFeasibleRegionEmpty() {
        this.graphicalAnswer = new GraphicalAnswer(GraphicalSolutionType.FEASIBLE_REGION_EMPTY);
        graphicalAnswer.setAnswer("Область допустимых решений - пустое множество.");
    }

    private double calcObjectiveValue(Point point) {
        return objectiveFunc.getA() * point.getX() + objectiveFunc.getB() * point.getY();
    }
}

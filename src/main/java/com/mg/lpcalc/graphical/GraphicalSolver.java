package com.mg.lpcalc.graphical;

import com.mg.lpcalc.graphical.model.*;
import com.mg.lpcalc.graphical.solution.GraphicalSolutionBuilder;
import com.mg.lpcalc.model.enums.Operator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphicalSolver {
    private final double EPS = 1e-9;
    private List<Constraint> constraints;
    private List<Constraint> currentConstraints = new ArrayList<>();
    private ObjectiveFunc objectiveFunc;
    private List<Point> currentFeasibleRegion = new ArrayList<>();
    private GraphicalSolutionBuilder solutionBuilder = new GraphicalSolutionBuilder();
    private double maxX;
    private double maxY;

    public GraphicalSolver(OptimizationProblem optimizationProblem) {
        this.constraints = new ArrayList<>(optimizationProblem.getConstraints());
        this.objectiveFunc = optimizationProblem.getObjectiveFunc();
    }

    public void solve() {
        List<Constraint> initialConstraints = initConstraints();

        for (Constraint c : initialConstraints) {
            addConstraint(c);
        }

        for (Constraint c : constraints) {
            System.out.println("НОВАЯ ПРЯМАЯ НОВАЯ ПРЯМАЯ");
            System.out.println(c);
            addConstraint(c);
        }

        List<Point> optimalPoints = findOptimalSolution();

        List<Point> allPoints = findAllFeasiblePoints();
        solutionBuilder.init(allPoints, objectiveFunc, optimalPoints);
    }

    private List<Point> findAllFeasiblePoints() {
        List<Point> axisIntersections = findAxisIntersections(this.constraints);

        List<Point> points = new ArrayList<>();
        points.addAll(axisIntersections);
        points.addAll(this.currentFeasibleRegion);

        return points.stream()
                .filter(p -> {
                    boolean isOnNegativeXAxis = (p.getX() == 0 && p.getY() <= 0);
                    boolean isOnNegativeYAxis = (p.getY() == 0 && p.getX() <= 0);
                    boolean isInFirstQuadrant = (p.getX() >= 0 && p.getY() >= 0);

                    return isOnNegativeXAxis || isOnNegativeYAxis || isInFirstQuadrant;
                })
                .toList();
    }

    private List<Constraint> initConstraints() {
        List<Constraint> initialConstraints = new ArrayList<>();
        List<Point> axisIntersections = findAxisIntersections(this.constraints);
        List<Point> constraintsIntersections = findConstraintIntersections(this.constraints);
        List<Point> allPoints = Stream.concat(axisIntersections.stream(), constraintsIntersections.stream()).toList();
        double maxX = allPoints.get(0).getX();
        double maxY = allPoints.get(0).getY();

        System.out.println("AXIS INTER");
        System.out.println(allPoints);
        for (Point point : allPoints) {
            if (point.getX() > maxX) maxX = point.getX();
            if (point.getY() > maxY) maxY = point.getY();
        }

        System.out.println(maxX);
        this.maxX = maxX;
        this.maxY = maxY;

        initialConstraints.add(new Constraint(1., 0., 0., Operator.GEQ, false, true));
        initialConstraints.add(new Constraint(0., 1., 0., Operator.GEQ, false, true));
        initialConstraints.add(new Constraint(0., 1., maxY, Operator.LEQ, true, true));
        initialConstraints.add(new Constraint(1., 0., maxX, Operator.LEQ, true, true));

        return initialConstraints;
    }

    private List<Point> findConstraintIntersections(List<Constraint> constraints) {
        List<Point> intersections = new ArrayList<>();

        for (int i = 0; i < constraints.size(); i++) {
            for (int j = i + 1; j < constraints.size(); j++) {
                Constraint c1 = constraints.get(i);
                Constraint c2 = constraints.get(j);

                // Вычисление determinant (определителя)
                double det = c1.getA() * c2.getB() - c2.getA() * c1.getB();

                if (Math.abs(det) > EPS) {
                    double x = (c2.getB() * c1.getC() - c1.getB() * c2.getC()) / det;
                    double y = (c1.getA() * c2.getC() - c2.getA() * c1.getC()) / det;

                    Point point = new Point(x, y);
                    boolean intersectionMaxCoordinate = (point.getX() == this.maxX || point.getY() == this.maxY)
                             && (c1.isInitial() ? isFillInAbove(c2) : isFillInAbove(c1));

                    if (c1.isUnbounded() || c2.isUnbounded()) {
                        point.setUnbounded(true);
                    } else if (intersectionMaxCoordinate) {
                        point.setFeasibleRegionIsAbove(true);
                    }

                    intersections.add(point);
                }
            }
        }

        System.out.println("INTER INTER");
        System.out.println(intersections);
        return intersections;
    }

    // Вычисление, сверху или снизу от прямой закрашивать ОДР
    private boolean isFillInAbove(Constraint constraint) {
        if (constraint.getB() == 0) return false;
        if (constraint.getOperator().equals(Operator.LEQ)) {
            return constraint.getB() < 0;
        } else {
            return constraint.getB() > 0;
        }
    }

    private List<Point> findAxisIntersections(List<Constraint> constraints) {
        List<Point> intersections = new ArrayList<>();

        for (Constraint c : constraints) {
            // Пересечение с осью X
            if (Math.abs(c.getA()) > EPS) {
                double x = c.getC() / c.getA();
                intersections.add(new Point(x, 0.0));
            }

            // Пересечение с осью Y
            if (Math.abs(c.getB()) > EPS) {
                double y = c.getC() / c.getB();
                intersections.add(new Point(0.0, y));
            }
        }

        return intersections;
    }

    // Проверка на то, является ли точка допустимой
    private boolean isFeasible(Point point, List<Constraint> constraints) {
        double x = point.getX();
        double y = point.getY();

        // Проверка неотрицательности переменных (x >= 0, y >= 0)
        if (x < -EPS || y < -EPS) return false;

        for (Constraint c : constraints) {
            double leftSide = c.getA() * x + c.getB() * y;

            if (c.isLEQ()) {
                if (leftSide > c.getC() + EPS) return false;
            } else {
                if (leftSide < c.getC() - EPS) return false;
            }
        }

        return true;
    }

    private void addConstraint(Constraint constraint) {
        this.currentConstraints.add(constraint);

        List<Point> newFeasibleRegion = new ArrayList<>();

        List<Point> intersections = findConstraintIntersections(this.currentConstraints);
        List<Point> points = new ArrayList<>();
        points.addAll(intersections);
        points.addAll(this.currentFeasibleRegion);

        for (Point point : points) {
            if (isFeasible(point, this.currentConstraints)) {
                newFeasibleRegion.add(point);
            }
        }

        this.currentFeasibleRegion = removeUnboundedDuplicates(newFeasibleRegion);

        if (!constraint.isInitial()) {
            List<Point> axisPoints = findAxisIntersections(currentConstraints.subList(3, currentConstraints.size()));
            solutionBuilder.addConstraint(constraint, currentFeasibleRegion, axisPoints);
        }
    }

    // Убираем дублирующие точки
    // Если есть две точки с одинаковыми координатами, но одна unbounded, а вторая нет - оставляем !unbounded
    public static List<Point> removeUnboundedDuplicates(List<Point> points) {
        return points.stream()
                .collect(Collectors.groupingBy(
                        p -> new AbstractMap.SimpleEntry<>(p.getX(), p.getY())
                ))
                .values()
                .stream()
                .map(group -> group.stream()
                        .filter(p -> !p.isUnbounded())
                        .findFirst()
                        .orElse(group.get(0)))
                .collect(Collectors.toList());
    }

    private List<Point> findOptimalSolution() {
        if (this.currentFeasibleRegion.isEmpty()) {
            System.out.println("Нет допустимых решений (ОДР пуста).");
        }

        List<Double> zValues = new ArrayList<>();
        for (Point point : this.currentFeasibleRegion) {
            zValues.add(objectiveFunc.getA() * point.getX() + objectiveFunc.getB() * point.getY());
        }

        double optimalValue = zValues.get(0);
        for (double z: zValues) {
            if (objectiveFunc.isMaximization() && z > optimalValue) {
                optimalValue = z;
            } else if (objectiveFunc.isMinimization() && z < optimalValue) {
                optimalValue = z;
            }
        }

        System.out.println(zValues);

        List<Point> optimalPoints = new ArrayList<>();
        for (int i = 0; i < zValues.size(); i++) {
            if (Math.abs(zValues.get(i) - optimalValue) < EPS) {
                optimalPoints.add(this.currentFeasibleRegion.get(i));
            }
        }

        if (optimalPoints.size() == 1) {
            System.out.println("Оптимальное решение: " + optimalPoints.get(0));
        } else {
            System.out.println("Множество оптимальных решений между вершинами: " + optimalPoints);
        }

        return optimalPoints;
    }
}

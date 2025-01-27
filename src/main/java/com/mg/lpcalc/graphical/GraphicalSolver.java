package com.mg.lpcalc.graphical;

import com.mg.lpcalc.graphical.model.*;

import java.util.ArrayList;
import java.util.List;

public class GraphicalSolver {
    private final double EPS = 1e-9;
    private List<Constraint> constraints;
    private List<Constraint> currentConstraints = new ArrayList<>();
    private ObjectiveFunc objectiveFunc;
    private GraphBounds graphBounds;
    private List<Point> currentFeasibleRegion;

    public GraphicalSolver(OptimizationProblem optimizationProblem) {
        this.constraints = optimizationProblem.getConstraints();
        this.objectiveFunc = optimizationProblem.getObjectiveFunc();
    }

    public void solve() {
        initGraphicBounds();
        initFeasibleRegion();

        for (Constraint c : constraints) {
            System.out.println("НОВАЯ ПРЯМАЯ НОВАЯ ПРЯМАЯ");
            addConstraint(c);
        }
    }

    private void initGraphicBounds() {
        List<Point> constraintIntersections = findConstraintIntersections(constraints);
        List<Point> axisIntersections = findAxisIntersections(constraints);
        List<Point> points = new ArrayList<>();
        points.addAll(constraintIntersections);
        points.addAll(axisIntersections);

        double minX = points.get(0).getX();
        double maxX = points.get(0).getX();
        double minY = points.get(0).getY();
        double maxY = points.get(0).getY();

        for (Point p : points) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }

        // График должен точку (0;0). Начало оси координат
        minX = Math.min(minX, 0);
        maxX = Math.max(maxX, 0);
        minY = Math.min(minY, 0);
        maxY = Math.max(maxY, 0);

        // todo решить что делать с отступами
//        double xRange = maxX - minX;
//        double yRange = maxY - minY;
//
//        double paddingPercentage = 0.25;
//        double paddingX = xRange * paddingPercentage;
//        double paddingY = yRange * paddingPercentage;

        this.graphBounds = new GraphBounds(
                minX, maxX,
                minY, maxY
        );
    }

    private void initFeasibleRegion() {
        Point leftBottom = new Point(0, 0);
        Point leftTop = new Point(0, Double.POSITIVE_INFINITY);
        Point rightTop = new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Point rightBottom = new Point(Double.POSITIVE_INFINITY, 0);

        this.currentFeasibleRegion = new ArrayList<>(List.of(leftBottom, leftTop, rightBottom, rightTop));
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

                    intersections.add(new Point(x, y));
                }
            }
        }

        return intersections;
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

    private boolean isFeasible(Point point, List<Constraint> constraints) {
        double x = point.getX();
        double y = point.getY();

        // Проверка неотрицательности переменных (x >= 0, y >= 0)
        if (x < -EPS || y < -EPS) return false;

        for (Constraint c : constraints) {
            double leftSide = c.getA() * x + c.getB() * y;

            if (c.isLessOrEqual()) {
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

        List<Point> points = findConstraintIntersections(this.currentConstraints);
        points.addAll(findAxisIntersections(List.of(constraint)));
        points.addAll(this.currentFeasibleRegion);

        for (Point point : points) {
            if (isFeasible(point, this.currentConstraints)) {
                newFeasibleRegion.add(point);
            }
        }

        this.currentFeasibleRegion = newFeasibleRegion;
        System.out.println(currentFeasibleRegion);
    }
}

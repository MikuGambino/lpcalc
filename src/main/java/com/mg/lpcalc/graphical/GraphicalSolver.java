package com.mg.lpcalc.graphical;

import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.ObjectiveFunc;
import com.mg.lpcalc.graphical.model.OptimizationProblem;
import com.mg.lpcalc.graphical.model.Point;

import java.util.ArrayList;
import java.util.List;

public class GraphicalSolver {
    private final double EPS = 1e-9;
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;

    public GraphicalSolver(OptimizationProblem optimizationProblem) {
        this.constraints = optimizationProblem.getConstraints();
        this.objectiveFunc = optimizationProblem.getObjectiveFunc();
    }

    public void solve() {

    }

    private void initGraphicCoordinates() {

    }

    public List<Point> findAllIntersections(List<Constraint> constraints) {
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

        // todo для отладки - удалить
        for (Point point : intersections) {
            System.out.println(point);
        }

        return intersections;
    }
}

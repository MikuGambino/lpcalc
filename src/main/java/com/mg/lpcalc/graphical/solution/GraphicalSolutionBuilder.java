package com.mg.lpcalc.graphical.solution;

import com.mg.lpcalc.graphical.graph.GraphBuilder;
import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.ObjectiveFunc;
import com.mg.lpcalc.graphical.model.Point;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    public void init(List<Point> points, ObjectiveFunc objectiveFunc, List<Point> optimalPoints) {
        this.graphBuilder = new GraphBuilder(points);
        for (int i = 0; i < constraints.size(); i++) {
            graphBuilder.addConstraint(constraints.get(i), feasibleRegions.get(i), axisPoints.get(i));
        }

        graphBuilder.addObjectiveFunc(objectiveFunc);
        graphBuilder.getFinalGraph(objectiveFunc, optimalPoints);
    }
}

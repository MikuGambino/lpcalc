package com.mg.lpcalc.graphical.solution;

import com.mg.lpcalc.graphical.graph.GraphDrawer;
import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.Point;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class GraphicalSolutionBuilder {
    private List<Constraint> constraints = new ArrayList<>();
    private GraphDrawer graphDrawer;

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    public void init(List<Point> points, double PADDING_PERCENTAGE) {
        this.graphDrawer = new GraphDrawer(points, PADDING_PERCENTAGE);
        for (Constraint constraint : constraints) {
            graphDrawer.addConstraint(constraint);
        }
    }
}

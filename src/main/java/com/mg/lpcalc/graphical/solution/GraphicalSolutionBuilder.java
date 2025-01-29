package com.mg.lpcalc.graphical.solution;

import com.mg.lpcalc.graphical.graph.GraphDrawer;
import com.mg.lpcalc.graphical.model.Point;

import java.util.List;

public class GraphicalSolutionBuilder {
    private GraphDrawer graphDrawer;

    public GraphicalSolutionBuilder(List<Point> points, double PADDING_PERCENTAGE) {
        this.graphDrawer = new GraphDrawer(points, PADDING_PERCENTAGE);
    }
}

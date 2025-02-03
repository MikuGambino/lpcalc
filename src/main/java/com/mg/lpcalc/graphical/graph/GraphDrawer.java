package com.mg.lpcalc.graphical.graph;

import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.Point;

import java.util.ArrayList;
import java.util.List;

public class GraphDrawer {
    private final int GRAPH_SIZE = 500;
    private final double PADDING_PERCENTAGE = 0.2;
    private GraphParams graphParams;
    private List<Graph> graphs = new ArrayList<>();

    public GraphDrawer(List<Point> points) {
        initGraphParams(points);
    }

    private void initGraphParams(List<Point> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Список точек не может быть пустым");
        }

        double maxX = points.get(0).getX();
        double maxY = points.get(0).getY();
        double minX = points.get(0).getX();
        double minY = points.get(0).getY();

        for (Point point : points) {
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
        }

        double xRange = maxX - minX;
        double yRange = maxY - minY;

        double maxRange = Math.max(xRange, yRange);

        double viewBoxSize = GRAPH_SIZE / 2.;
        double pxSize = viewBoxSize / maxRange;
        pxSize -= pxSize * PADDING_PERCENTAGE;

        ViewBoxParams viewBoxParams = new ViewBoxParams(minX, minY, viewBoxSize, pxSize, PADDING_PERCENTAGE);

        this.graphParams = GraphParams.builder()
                .maxX(maxX)
                .minX(minX)
                .minY(minY)
                .maxY(maxY)
                .maxPadding(maxRange)
                .pxSize(pxSize)
                .viewBoxParams(viewBoxParams)
                .graphSize(GRAPH_SIZE)
                .build();
    }

    public String addConstraint(Constraint constraint) {
        List<Point> linePoints = findLinePoints(constraint);
        Line line = new Line(
                linePoints.get(0).getX(),
                linePoints.get(0).getY(),
                linePoints.get(1).getX(),
                linePoints.get(1).getY()
            );

        List<Line> graphLines = new ArrayList<>();
        if (!this.graphs.isEmpty()) {
            Graph lastGraph = this.graphs.get(graphs.size() - 1);
            graphLines = new ArrayList<>(lastGraph.getLines());
        }

        graphLines.add(line);
        Graph newGraph = new Graph(graphParams, graphLines);
        this.graphs.add(newGraph);
        System.out.println(newGraph.getSVG());
        System.out.println("-------------------");
        return newGraph.getSVG();
    }


    public List<Point> findLinePoints(Constraint constraint) {
        List<Point> points = new ArrayList<>();
        ViewBoxParams viewBox = graphParams.getViewBoxParams();
        double minX = toCords(viewBox.getMinX() - viewBox.getSize());
        double minY = toCords(viewBox.getMinY() - viewBox.getSize());
        double maxX = toCords(viewBox.getMinX());
        double maxY = toCords(viewBox.getMinY());

        if (constraint.getB() != 0) {
            double yAtMinX = (-constraint.getA() * minX + constraint.getC()) / constraint.getB();
            points.add(toPx(new Point(minX, yAtMinX)));

            double yAtMaxX = (-constraint.getA() * maxX + constraint.getC()) / constraint.getB();
            points.add(toPx(new Point(maxX, yAtMaxX)));
        }
        else if (constraint.getA() != 0) {
            double xAtMinY  = (-constraint.getB() * minY + constraint.getC()) / constraint.getA();
            points.add(toPx(new Point(xAtMinY, minY)));

            double xAtMaxY = (-constraint.getB() * maxY + constraint.getC()) / constraint.getA();
            points.add(toPx(new Point(xAtMaxY, maxY)));
        }

        return points;
    }

    private Point toPx(Point point) {
        return new Point(point.getX() * graphParams.getPxSize(), point.getY() * graphParams.getPxSize());
    }

    private double toCords(Double num) {
        return num / graphParams.getPxSize();
    }
}

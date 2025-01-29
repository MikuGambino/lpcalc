package com.mg.lpcalc.graphical.graph;

import com.mg.lpcalc.graphical.model.Point;

import java.util.List;

public class GraphDrawer {
    private final int GRAPH_SIZE = 500;
    private final double PADDING_PERCENTAGE;
    private GraphParams graphParams;

    public GraphDrawer(List<Point> points, double PADDING_PERCENTAGE) {
        this.PADDING_PERCENTAGE = PADDING_PERCENTAGE;
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
        pxSize -= pxSize * (PADDING_PERCENTAGE - 1);

        ViewBoxParams viewBoxParams = new ViewBoxParams(minX, minY, viewBoxSize, pxSize, PADDING_PERCENTAGE);

        this.graphParams = GraphParams.builder()
                .maxX(maxX)
                .minX(minX)
                .minY(minY)
                .maxY(maxY)
                .maxPadding(maxRange)
                .pxSize(pxSize)
                .viewBoxParams(viewBoxParams)
                .build();

        System.out.println(graphParams);
    }
}

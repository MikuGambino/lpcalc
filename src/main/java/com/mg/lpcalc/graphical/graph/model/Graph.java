package com.mg.lpcalc.graphical.graph.model;

import com.mg.lpcalc.graphical.graph.GraphParams;
import com.mg.lpcalc.graphical.graph.SVGCode;
import com.mg.lpcalc.graphical.graph.ViewBoxParams;
import com.mg.lpcalc.graphical.model.Point;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class Graph implements SVGElement {
    private GraphParams graphParams;
    private StringBuilder svg = new StringBuilder();
    private List<Line> lines;
    private Polygon feasibleRegion;
    private List<Circle> feasibleRegionPoints;
    private List<Circle> axisPoints;

    public Graph(GraphParams graphParams, List<Line> lines, Polygon feasibleRegion,
                 List<Circle> feasibleRegionPoints, List<Circle> axisPoints) {
        this.lines = lines;
        this.feasibleRegion = feasibleRegion;
        this.graphParams = graphParams;
        this.feasibleRegionPoints = feasibleRegionPoints;
        this.axisPoints = axisPoints;
    }

    private void addSvgHeader() {
        ViewBoxParams viewBox = graphParams.getViewBoxParams();
        svg.append(String.format(Locale.US,
                SVGCode.SVG_HEADER,
                viewBox.getMinX(),
                viewBox.getMinY(),
                viewBox.getSize(),
                viewBox.getSize(),
                graphParams.getGraphSize(),
                graphParams.getGraphSize())
        );
    }

    private void addAxis() {
        // Ось x
        Line xAxis = new Line(0, 250, 500, 250, 1);
        svg.append("\t");
        svg.append(xAxis.toSVG());

        // Ось y
        Line yAxis = new Line(250, 0, 250, 500, 1);
        svg.append("\t");
        svg.append(yAxis.toSVG());
    }

    private void addLines() {
        for (Line line : lines) {
            svg.append("\t\t");
            svg.append(line.toSVG());
        }
    }

    private void addGroup() {
        svg.append("\t");
        svg.append(String.format(Locale.US, SVGCode.GROUP,
                graphParams.getGraphSize() / 2., graphParams.getGraphSize() / 2.));
    }

    private void addFeasibleRegion() {
        svg.append("\t\t");
        svg.append(feasibleRegion.toSVG());
    }

    private void addFeasibleRegionPoints() {
        for (Circle circle : feasibleRegionPoints) {
            svg.append("\t\t");
            svg.append(circle.toSVG());
        }
    }

    private void addAxisPoints() {
        for (Circle circle : axisPoints) {
            svg.append("\t\t");
            circle.setR(1);
            circle.setFill("black");
            svg.append(circle.toSVG());

            Text pointLabel = null;
            boolean isVerticalAxis = circle.getCx() == 0;
            boolean isHorizontalAxis = circle.getCy() == 0;

            if (isVerticalAxis || isHorizontalAxis) {
                double coordinate = isVerticalAxis ? circle.getYCoordinate() : circle.getXCoordinate();
                // Если координата целая – выводим целое значение, иначе выводим дробное
                String text = (coordinate % 1 == 0) ? String.valueOf((int) coordinate) : String.valueOf(coordinate);

                if (isVerticalAxis) {
                    pointLabel = new Text(-8, -circle.getCy() + circle.getR() * 2, "axis", text);
                } else if (isHorizontalAxis) {
                    pointLabel = new Text(circle.getCx() - circle.getR() * 2, 8, "axis", text);
                }
            }

            if (pointLabel != null) {
                svg.append("\t\t");
                svg.append(pointLabel.toSVG());
            }
        }
    }

    public String toSVG() {
        addSvgHeader();
        addAxis();
        addGroup();
        addLines();
        addAxisPoints();
        addFeasibleRegion();
        addFeasibleRegionPoints();
        return svg.toString();
    }
}

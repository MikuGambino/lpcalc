package com.mg.lpcalc.graphical.model.graph;

import com.mg.lpcalc.graphical.graph.SVGCode;
import com.mg.lpcalc.graphical.graph.ViewBoxParams;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class Graph implements SVGElement {
    private GraphParams graphParams;
    private StringBuilder svg;
    private List<Line> lines;
    private Polygon feasibleRegion;
    private List<Circle> feasibleRegionPoints;
    private List<Circle> axisPoints;
    private Arrow objectiveFunc;
    private Line perpendicularLine;
    private List<Circle> optimalPoints;

    public Graph(GraphParams graphParams, List<Line> lines, Polygon feasibleRegion,
                 List<Circle> feasibleRegionPoints, List<Circle> axisPoints) {
        this.lines = lines;
        this.feasibleRegion = feasibleRegion;
        this.graphParams = graphParams;
        this.feasibleRegionPoints = feasibleRegionPoints;
        this.axisPoints = axisPoints;
    }

    private void addSvgMetadata() {
        ViewBoxParams viewBox = graphParams.getViewBoxParams();
        svg.append(String.format(Locale.US,
                SVGCode.SVG_METADATA,
                viewBox.getMinX(),
                viewBox.getMinY(),
                viewBox.getSize(),
                viewBox.getSize(),
                graphParams.getGraphSize(),
                graphParams.getGraphSize())
        );
    }

    private void addAxis() {
        ViewBoxParams viewBox = graphParams.getViewBoxParams();

        // Ось x
        Axis xAxis = new Axis(
                0,
                graphParams.getGraphSize() / 2,
                viewBox.getSize() + viewBox.getMinX() - 5,
                graphParams.getGraphSize() / 2
        );
        svg.append("\t");
        svg.append(xAxis.toSVG());

        // Ось y
        Axis yAxis = new Axis(
                graphParams.getGraphSize() / 2,
                0,
                graphParams.getGraphSize() / 2,
                viewBox.getSize() + viewBox.getMinY() - 5
        );
        svg.append("\t");
        svg.append(yAxis.toSVG());
    }

    private void addLines() {
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            String label = "(%s)".formatted(i + 1);
            Text text = getText(line, label);
            svg.append("\t\t");
            svg.append(line.toSVG());
            svg.append("\t\t");
            svg.append(text.toSVG());
        }
    }

    private Text getText(Line line, String label) {
        double x = line.getX1() + 4;
        double y = line.getY1() > 0 ? line.getY1() - 10 : line.getY1() + 10;
        // Если конец линии выше видимой области
        if (y > graphParams.getViewBoxParams().getMinY()) {
            x = -20;
            y = graphParams.getViewBoxParams().getMinY() - 10;
        } else if (y < graphParams.getMinY() * getGraphParams().getPxSize()) {
            x = -20;
            y = graphParams.getMinY() * getGraphParams().getPxSize() - 10;
        }

        Text text;
        if (line.getX1() == line.getX2()) {
            text = new Text(line.getX1() + 2, -line.getY1() - 5, "line-label", label);
        } else if (line.getY1() == line.getY2()) {
            text = new Text(line.getX1() + 2, -line.getY1() - 5, "line-label", label);
        } else {
            text = new Text(x, -y, "line-label", label);
        }
        return text;
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

    private void addObjectiveFunc() {
        svg.append("\t\t");
        svg.append(objectiveFunc.toSVG());
    }

    private void addPerpendicularLine() {
        svg.append("\t\t");
        perpendicularLine.setStroke("#c20000");
        perpendicularLine.setStrokeWidth(0.5);
        svg.append(perpendicularLine.toSVG());
    }

    private void addOptimalPoints() {
        for (Circle circle : optimalPoints) {
            svg.append("\t\t");
            circle.setFill("#c20000");
            svg.append(circle.toSVG());
        }
    }

    public String toSVG() {
        this.svg = new StringBuilder();
        addSvgMetadata();
        addAxis();
        addGroup();
        addFeasibleRegion();
        addLines();
        addAxisPoints();
        addFeasibleRegionPoints();
        if (objectiveFunc != null) {
            addObjectiveFunc();
        }
        if (perpendicularLine != null) {
            addPerpendicularLine();
        }
        if (optimalPoints != null) {
            addOptimalPoints();
        }
        svg.append("</g></svg>");
        return svg.toString();
    }
}

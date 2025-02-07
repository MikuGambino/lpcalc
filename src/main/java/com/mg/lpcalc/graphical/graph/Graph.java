package com.mg.lpcalc.graphical.graph;

import com.mg.lpcalc.graphical.model.Point;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class Graph {
    private GraphParams graphParams;
    private StringBuilder svg = new StringBuilder();
    private List<Line> lines;
    private Polygon feasibleRegion;

    public Graph(GraphParams graphParams, List<Line> lines, Polygon feasibleRegion) {
        this.lines = lines;
        this.feasibleRegion = feasibleRegion;
        this.graphParams = graphParams;
    }

    private void addSvgHeader() {
        ViewBoxParams viewBox = graphParams.viewBoxParams;
        svg.append(String.format(Locale.US,
                SVGCode.SVG_HEADER,
                viewBox.minX,
                viewBox.minY,
                viewBox.size,
                viewBox.size,
                graphParams.graphSize,
                graphParams.graphSize)
        );
    }

    private void addAxis() {
        // Ось x
        Line xAxis = new Line(0, 250, 500, 250);
        svg.append("\t");
        svg.append(xAxis.toSVG());

        // Ось y
        Line yAxis = new Line(250, 0, 250, 500);
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
        svg.append(String.format(Locale.US, SVGCode.GROUP, graphParams.graphSize / 2., graphParams.graphSize / 2.));
    }

    private void addFeasibleRegion() {
        svg.append("\t\t");
        StringBuilder pointsString = new StringBuilder();
        for (Point p : feasibleRegion.getPoints()) {
            pointsString.append(String.format(Locale.US,"%.2f,%.2f ", p.getX(), p.getY()));
        }

        svg.append(String.format(Locale.US, SVGCode.POLYGON,
                feasibleRegion.getFill(),
                feasibleRegion.getOpacity(),
                pointsString
            ));
    }

    public String getSVG() {
        addSvgHeader();
        addAxis();
        addGroup();
        addLines();
        addFeasibleRegion();
        return svg.toString();
    }
}

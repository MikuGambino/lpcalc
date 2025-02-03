package com.mg.lpcalc.graphical.graph;

import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class Graph {
    private GraphParams graphParams;
    private StringBuilder svg = new StringBuilder();
    private List<Line> lines;

    public Graph(GraphParams graphParams, List<Line> lines) {
        this.lines = lines;
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

    public String getSVG() {
        addSvgHeader();
        addAxis();
        addGroup();
        addLines();
        return svg.toString();
    }
}

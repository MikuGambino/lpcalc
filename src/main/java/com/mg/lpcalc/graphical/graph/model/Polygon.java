package com.mg.lpcalc.graphical.graph.model;

import com.mg.lpcalc.graphical.graph.SVGCode;
import com.mg.lpcalc.graphical.solver.model.Point;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class Polygon implements SVGElement {
    private List<Point> points;
    private String fill = "#9DC08B";
    private double opacity = 1;

    public Polygon(List<Point> points) {
        this.points = points;
    }

    public String toSVG() {
        StringBuilder pointsString = new StringBuilder();
        for (Point p : points) {
            pointsString.append(String.format(Locale.US,"%.2f,%.2f ", p.getX(), p.getY()));
        }

        return String.format(Locale.US, SVGCode.POLYGON, fill, opacity, pointsString);
    }
}

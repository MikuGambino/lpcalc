package com.mg.lpcalc.graphical.model.graph;

import com.mg.lpcalc.graphical.graph.SVGCode;
import com.mg.lpcalc.graphical.model.Point;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class Polygon implements SVGElement {
    private List<Point> points;
    private String fill = "#3E7CB4";
    private double opacity = 0.8;

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

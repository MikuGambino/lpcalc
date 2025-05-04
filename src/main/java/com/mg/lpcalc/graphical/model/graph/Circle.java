package com.mg.lpcalc.graphical.model.graph;

import com.mg.lpcalc.graphical.graph.SVGCode;
import lombok.Data;

import java.util.Locale;

@Data
public class Circle implements SVGElement {
    private double cx;
    private double cy;
    private double xCoordinate;
    private double yCoordinate;
    private double r = 2;
    private String fill = "#F17C3A";
    private String label;

    public Circle(double cx, double cy, double xCoordinate, double yCoordinate) {
        this.cx = cx;
        this.cy = cy;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public Circle(double cx, double cy) {
        this.cx = cx;
        this.cy = cy;
    }

    public String toSVG() {
        StringBuilder svg = new StringBuilder();
        if (label != null) {
            Text text = new Text(cx + 1, -cy - 4, "no-stroke", label);
            svg.append(text.toSVG());
        }

        return svg.append(String.format(Locale.US, SVGCode.CIRCLE, cx, cy, r, fill)).toString();
    }
}

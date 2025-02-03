package com.mg.lpcalc.graphical.graph;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
public class Line {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private String stroke = "black";
    private double strokeWidth = 1.;

    public Line(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public String toSVG() {
        return String.format(Locale.US, SVGCode.LINE, x1, y1, x2, y2, stroke, strokeWidth);
    }
}

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
    private String fill = "#DF6D14";

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
        return String.format(Locale.US, SVGCode.CIRCLE, cx, cy, r, fill);
    }
}

package com.mg.lpcalc.graphical.graph.model;

import com.mg.lpcalc.graphical.graph.SVGCode;
import com.mg.lpcalc.graphical.model.Point;
import lombok.Data;

import java.util.Locale;

@Data
public class Line implements SVGElement {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private String stroke = "black";
    private double strokeWidth = 0.5;

    public Line(Point begin, Point end) {
        this.x1 = begin.getX();
        this.y1 = begin.getY();
        this.x2 = end.getX();
        this.y2 = end.getY();
    }

    public Point getBeginPoint() {
        return new Point(x1, y1);
    }

    public Point getEndPoint() {
        return new Point(x2, y2);
    }

    public String toSVG() {
        return String.format(Locale.US, SVGCode.LINE, x1, y1, x2, y2, stroke, strokeWidth);
    }
}

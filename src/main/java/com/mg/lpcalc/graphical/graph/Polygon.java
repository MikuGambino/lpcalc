package com.mg.lpcalc.graphical.graph;

import com.mg.lpcalc.graphical.model.Point;
import lombok.Data;

import java.util.List;

@Data
public class Polygon {
    private List<Point> points;
    private String fill = "#008000";
    private double opacity = 0.5;

    public Polygon(List<Point> points) {
        this.points = points;
    }
}

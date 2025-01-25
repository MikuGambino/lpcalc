package com.mg.lpcalc.graphical.model;

import lombok.Data;

@Data
public class GraphBounds {
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public GraphBounds(double minX, double maxX, double minY, double maxY, double padding) {
        this.minX = minX - padding;
        this.maxX = maxX + padding;
        this.minY = minY - padding;
        this.maxY = maxY + padding;

        System.out.println("minX: " + this.minX);
        System.out.println("maxX: " + this.maxX);
        System.out.println("minY: " + this.minY);
        System.out.println("maxY: " + this.maxY);

    }
}

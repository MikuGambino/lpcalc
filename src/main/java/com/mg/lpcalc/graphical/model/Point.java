package com.mg.lpcalc.graphical.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class Point {
    private double x;
    private double y;
    private boolean isUnbounded;
    private boolean feasibleRegionIsAbove;
    private HashSet<Constraint> constraints = new HashSet<>();

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        if (this.x == -0) this.x = 0;
        if (this.y == -0) this.y = 0;
        this.isUnbounded = false;
    }

    public void addConstraints(Constraint... constraints) {
        this.constraints.addAll(List.of(constraints));
    }

    // Копирующий конструктор
    public Point(Point point) {
        this.x = point.getX();
        this.y = point.getY();
        this.isUnbounded = point.isUnbounded();
        this.feasibleRegionIsAbove = point.isFeasibleRegionIsAbove();
        this.constraints = point.getConstraints();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

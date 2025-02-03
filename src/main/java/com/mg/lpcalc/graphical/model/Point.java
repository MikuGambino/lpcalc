package com.mg.lpcalc.graphical.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class Point {
    double x;
    double y;
    boolean isUnbounded;

    // todo добавить точке список прямых

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        if (this.x == -0) this.x = 0;
        if (this.y == -0) this.y = 0;
        this.isUnbounded = false;
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

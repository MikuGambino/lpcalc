package com.mg.lpcalc.graphical.graph.model;

import com.mg.lpcalc.graphical.graph.SVGCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@AllArgsConstructor
@Data
public class Axis implements SVGElement {
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    @Override
    public String toSVG() {
        StringBuilder svg = new StringBuilder();
        // Если ось X
        String label = x1 == 0 ? "X₁" : "X₂";
        Text text = new Text(x2 - 8, -(y2 - 6), "axis", label);;
        svg.append(text.toSVG());

        svg.append("\t");
        svg.append(String.format(Locale.US, SVGCode.AXIS, x1, y1, x2, y2));
        return svg.toString();
    }
}

package com.mg.lpcalc.graphical.model.graph;

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
        Text text;
        // Если ось X
        if (x1 == 0) {
            text = new Text(x2 - 5, -(y2 - 8), "axis", "X₁");
        } else {
            text = new Text(x2 - 10, -y2, "axis", "X₂");
        }

        svg.append(text.toSVG());

        svg.append("\t");
        svg.append(String.format(Locale.US, SVGCode.AXIS, x1, y1, x2, y2));
        return svg.toString();
    }
}

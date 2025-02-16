package com.mg.lpcalc.graphical.graph.model;

import com.mg.lpcalc.graphical.graph.SVGCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor
public class Arrow implements SVGElement {
    private double x2;
    private double y2;

    @Override
    public String toSVG() {
        StringBuilder svg = new StringBuilder();
        Text text = new Text(x2 + 5, -y2, "no-stroke", "F");
        svg.append(text.toSVG());
        svg.append("\t\t");
        svg.append(String.format(Locale.US, SVGCode.OBJECTIVE, x2, y2));
        return svg.toString();
    }
}

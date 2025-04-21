package com.mg.lpcalc.graphical.model.graph;

import com.mg.lpcalc.graphical.graph.SVGCode;
import lombok.Data;

import java.util.Locale;

@Data
public class Text implements SVGElement {
    private double x;
    private double y;
    private String clazz;
    private String text;

    public Text(double x, double y, String clazz, String text) {
        this.x = x;
        this.y = y;
        this.clazz = clazz;
        this.text = text;
    }

    public String toSVG() {
        return String.format(Locale.US, SVGCode.TEXT, x, y, clazz, text);
    }
}

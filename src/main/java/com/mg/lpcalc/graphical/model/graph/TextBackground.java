package com.mg.lpcalc.graphical.model.graph;

import com.mg.lpcalc.graphical.graph.SVGCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor
public class TextBackground {
    private double x;
    private double y;
    private double width;
    private double height;

    public String toSVG() {
        return String.format(Locale.US, SVGCode.TEXT_BACKGROUND, x, y, width, height);
    }
}

package com.mg.lpcalc.graphical.model.graph;

import com.mg.lpcalc.graphical.graph.ViewBoxParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GraphParams {
    private double maxX;
    private double maxY;
    private double minX;
    private double minY;
    private double paddingPx;
    private double pxSize;
    private int graphSize;
    private ViewBoxParams viewBoxParams;
}

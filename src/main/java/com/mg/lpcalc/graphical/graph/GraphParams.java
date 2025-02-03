package com.mg.lpcalc.graphical.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GraphParams {
    double maxX;
    double maxY;
    double minX;
    double minY;
    double maxPadding;
    double pxSize;
    int graphSize;
    ViewBoxParams viewBoxParams;
}

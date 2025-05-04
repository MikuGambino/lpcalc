package com.mg.lpcalc.graphical.model.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SegmentSolution {
    private PointSolution pointSolution1;
    private PointSolution pointSolution2;
}

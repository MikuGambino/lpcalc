package com.mg.lpcalc.graphical.model.solution;

import com.mg.lpcalc.graphical.model.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointSolution {
    private Point point;
    private String objectiveFuncValue;
    private FindPointCoordinates findPointCoordinates;
}

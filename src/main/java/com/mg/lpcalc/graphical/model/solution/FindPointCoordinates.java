package com.mg.lpcalc.graphical.model.solution;

import com.mg.lpcalc.graphical.model.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindPointCoordinates {
    private boolean pointOnAxis;
    private String systemOfEquationLatex;
    private Point point;
    private int constraintNumber1;
    private int constraintNumber2;

    public FindPointCoordinates(boolean pointOnAxis, Point point) {
        this.pointOnAxis = pointOnAxis;
        this.point = point;
    }


}

package com.mg.lpcalc.graphical.model;

import com.mg.lpcalc.model.enums.Direction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ObjectiveFunc {
    private List<Double> coefficients;
    private Direction direction;

    public Double getA() {
        return coefficients.get(0);
    }

    public Double getB() {
        return coefficients.get(1);
    }

    public boolean isMaximization() {
        return direction.equals(Direction.MAX);
    }

    public boolean isMinimization() {
        return direction.equals(Direction.MIN);
    }
}

package com.mg.lpcalc.simplex.model;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.ObjectiveFuncDTO;
import com.mg.lpcalc.model.enums.Direction;
import lombok.Data;

import java.util.List;

@Data
public class ObjectiveFunc {
    private List<Fraction> coefficients;
    private Direction direction;

    public ObjectiveFunc(ObjectiveFuncDTO objectiveFuncDTO) {
        this.coefficients = objectiveFuncDTO.getCoefficients();
        this.direction = objectiveFuncDTO.getDirection();
    }
}

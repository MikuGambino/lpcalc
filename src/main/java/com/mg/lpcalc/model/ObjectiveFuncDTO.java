package com.mg.lpcalc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mg.lpcalc.model.enums.Direction;
import lombok.Data;

import java.util.List;

@Data
public class ObjectiveFuncDTO {
    @JsonProperty("coefficients")
    private List<Fraction> coefficients;
    @JsonProperty("direction")
    private Direction direction;

    @Override
    public String toString() {
        return "ObjectiveFunc{" +
                "coefficients=" + coefficients +
                ", direction='" + direction.getTitle() + '\'' +
                '}';
    }
}

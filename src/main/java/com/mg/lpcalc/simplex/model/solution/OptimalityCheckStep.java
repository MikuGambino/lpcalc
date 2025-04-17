package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.enums.Direction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptimalityCheckStep {
    private boolean isOptimal;
    private Direction direction;
}

package com.mg.lpcalc.simplex.model.solution.basic;

import com.mg.lpcalc.simplex.model.Constraint;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConstraintTransformStep {
    private List<Constraint> constraints;
    private boolean constraintsIsChanged;
}

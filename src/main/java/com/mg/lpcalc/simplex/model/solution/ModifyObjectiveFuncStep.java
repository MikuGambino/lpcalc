package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyObjectiveFuncStep {
    private ObjectiveFunc objectiveFunc;
    private int artVariablesCount;
}

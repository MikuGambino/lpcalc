package com.mg.lpcalc.graphical.model.solution;

import com.mg.lpcalc.graphical.model.ObjectiveFunc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddObjectiveFunc {
    private ObjectiveFunc objectiveFunc;
    private String graph;
    private boolean inScale;
}

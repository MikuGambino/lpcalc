package com.mg.lpcalc.simplex.model.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BigMSolution implements Solution {
    private AddArtAndSlackVariablesStep addArtAndSlackVariablesStep = new AddArtAndSlackVariablesStep();

}

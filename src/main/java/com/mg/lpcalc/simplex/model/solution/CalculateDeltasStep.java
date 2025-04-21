package com.mg.lpcalc.simplex.model.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateDeltasStep {
    private List<List<String>> varLabels = new ArrayList<>();
    private List<List<FractionM>> varValues = new ArrayList<>();
    private List<FractionM> columnCost = new ArrayList<>();
}

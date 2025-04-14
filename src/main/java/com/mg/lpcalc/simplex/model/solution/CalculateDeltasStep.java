package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.Fraction;
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
    private List<List<Fraction>> varValues = new ArrayList<>();
    private List<Fraction> columnCost = new ArrayList<>();
}

package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.simplex.table.SimplexTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FindBasisStep {
    private SimplexTableDTO slackBasisTable;
    private List<FindBasisSubStep> subSteps;
}

package com.mg.lpcalc.graphical.model.solution;

import com.mg.lpcalc.model.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindInequalityRegion {
    private List<String> steps = new ArrayList<>();
    private Operator region;

    public FindInequalityRegion(Operator region) {
        this.region = region;
    }
}

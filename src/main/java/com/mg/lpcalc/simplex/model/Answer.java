package com.mg.lpcalc.simplex.model;

import com.mg.lpcalc.model.Fraction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Answer {
    private List<Fraction> variablesValues;
    private Fraction objectiveFuncValue;
}

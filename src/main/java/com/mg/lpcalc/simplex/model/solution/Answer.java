package com.mg.lpcalc.simplex.model.solution;

import com.mg.lpcalc.model.Fraction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    private AnswerType answerType;
    private List<Fraction> variablesValues;
    private Fraction objectiveValue;

    public Answer(AnswerType answerType) {
        this.answerType = answerType;
    }
}

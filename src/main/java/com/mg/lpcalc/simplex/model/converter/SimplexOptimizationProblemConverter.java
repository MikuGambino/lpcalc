package com.mg.lpcalc.simplex.model.converter;

import com.mg.lpcalc.model.OptimizationProblemDTO;
import com.mg.lpcalc.simplex.model.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimplexOptimizationProblemConverter implements Converter<OptimizationProblemDTO, OptimizationProblem> {
    public OptimizationProblem convert(OptimizationProblemDTO problemDTO) {
        List<Constraint> constraints = problemDTO.getConstraints()
                .stream()
                .map(Constraint::new)
                .toList();

        ObjectiveFunc objectiveFunc = new ObjectiveFunc(problemDTO.getObjectiveFunc());

        return OptimizationProblem.builder()
                .objectiveFunc(objectiveFunc)
                .constraints(constraints)
                .build();
    }
}

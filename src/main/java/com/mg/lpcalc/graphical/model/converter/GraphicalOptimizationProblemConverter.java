package com.mg.lpcalc.graphical.model.converter;

import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.ObjectiveFunc;
import com.mg.lpcalc.graphical.model.OptimizationProblem;
import com.mg.lpcalc.model.ObjectiveFuncDTO;
import com.mg.lpcalc.model.OptimizationProblemDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphicalOptimizationProblemConverter implements Converter<OptimizationProblemDTO, OptimizationProblem> {

    @Override
    public OptimizationProblem convert(OptimizationProblemDTO problemDTO) {
        List<Constraint> constraints = problemDTO.getConstraints()
                .stream()
                .map(constraintDTO -> Constraint.builder()
                        .rhs(constraintDTO.getRhs())
                        .operator(constraintDTO.getOperator())
                        .coefficients(constraintDTO.getCoefficients())
                        .build())
                .toList();

        ObjectiveFunc objectiveFunc = ObjectiveFunc.builder()
                .coefficients(problemDTO.getObjectiveFunc().getCoefficients())
                .direction(problemDTO.getObjectiveFunc().getDirection())
                .build();

        return OptimizationProblem.builder()
                .objectiveFunc(objectiveFunc)
                .constraints(constraints)
                .build();
    }
}

package com.mg.lpcalc.graphical.solver.model.converter;

import com.mg.lpcalc.graphical.solver.model.Constraint;
import com.mg.lpcalc.graphical.solver.model.ObjectiveFunc;
import com.mg.lpcalc.graphical.solver.model.OptimizationProblem;
import com.mg.lpcalc.model.Fraction;
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
                        .a(constraintDTO.getCoefficients().get(0).doubleValue())
                        .b(constraintDTO.getCoefficients().get(1).doubleValue())
                        .c(constraintDTO.getRhs().doubleValue())
                        .operator(constraintDTO.getOperator())
                        .isUnbounded(false)
                        .build())
                .toList();

        ObjectiveFunc objectiveFunc = ObjectiveFunc.builder()
                .coefficients(problemDTO.getObjectiveFunc().getCoefficients()
                        .stream()
                        .map(Fraction::doubleValue)
                        .toList()
                )
                .direction(problemDTO.getObjectiveFunc().getDirection())
                .build();

        return OptimizationProblem.builder()
                .objectiveFunc(objectiveFunc)
                .constraints(constraints)
                .build();
    }
}

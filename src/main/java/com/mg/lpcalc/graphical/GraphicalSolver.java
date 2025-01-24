package com.mg.lpcalc.graphical;

import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.converter.GraphicalOptimizationProblemConverter;
import com.mg.lpcalc.model.OptimizationProblemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphicalSolver {
    private final GraphicalOptimizationProblemConverter converter;
    private List<Constraint> constraints;
}

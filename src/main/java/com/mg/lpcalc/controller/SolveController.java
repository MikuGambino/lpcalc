package com.mg.lpcalc.controller;

import com.mg.lpcalc.graphical.model.converter.GraphicalOptimizationProblemConverter;
import com.mg.lpcalc.graphical.model.solution.GraphicalSolution;
import com.mg.lpcalc.graphical.solver.GraphicalSolver;
import com.mg.lpcalc.model.OptimizationProblemDTO;
import com.mg.lpcalc.simplex.model.converter.SimplexOptimizationProblemConverter;
import com.mg.lpcalc.simplex.model.solution.SimplexSolution;
import com.mg.lpcalc.simplex.solver.SimplexSolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/solve")
public class SolveController {

    private final GraphicalOptimizationProblemConverter graphicalConverter;
    private final SimplexOptimizationProblemConverter simplexConverter;

    @PostMapping("/graph")
    public GraphicalSolution graphSolve(@RequestBody OptimizationProblemDTO optimizationProblemDTO) {
        GraphicalSolver graphicalSolver = new GraphicalSolver(graphicalConverter.convert(optimizationProblemDTO));
        return graphicalSolver.solve();
    }

    @PostMapping("/simplex")
    public SimplexSolution simplexSolve(@RequestBody OptimizationProblemDTO optimizationProblemDTO) {
        SimplexSolver simplexSolver = new SimplexSolver(simplexConverter.convert(optimizationProblemDTO));
        return simplexSolver.solve();
    }
}

package com.mg.lpcalc.controller;

import com.mg.lpcalc.graphical.solver.GraphicalSolver;
import com.mg.lpcalc.graphical.solver.model.converter.GraphicalOptimizationProblemConverter;
import com.mg.lpcalc.model.OptimizationProblemDTO;
import com.mg.lpcalc.simplex.model.solution.Solution;
import com.mg.lpcalc.simplex.solver.SimplexSolver;
import com.mg.lpcalc.simplex.model.converter.SimplexOptimizationProblemConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> graphSolve(@RequestBody OptimizationProblemDTO optimizationProblemDTO) {
        GraphicalSolver graphicalSolver = new GraphicalSolver(graphicalConverter.convert(optimizationProblemDTO));
        graphicalSolver.solve();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simplex")
    public Solution simplexSolve(@RequestBody OptimizationProblemDTO optimizationProblemDTO) {
        SimplexSolver simplexSolver = new SimplexSolver(simplexConverter.convert(optimizationProblemDTO));
        Solution solution = simplexSolver.solve();
        System.out.println(solution);
        return solution;
    }
}

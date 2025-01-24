package com.mg.lpcalc.controller;

import com.mg.lpcalc.graphical.GraphicalSolver;
import com.mg.lpcalc.graphical.model.OptimizationProblem;
import com.mg.lpcalc.graphical.model.converter.GraphicalOptimizationProblemConverter;
import com.mg.lpcalc.model.OptimizationProblemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RequiredArgsConstructor
@RestController
@RequestMapping("/solve")
public class SolveController {

    private final GraphicalOptimizationProblemConverter converter;

    @PostMapping("/graph")
    public ResponseEntity<?> graphSolve(@RequestBody OptimizationProblemDTO optimizationProblemDTO) {
        Logger.getAnonymousLogger().info(converter.convert(optimizationProblemDTO).toString());
        OptimizationProblem optimizationProblem = converter.convert(optimizationProblemDTO);
        GraphicalSolver graphicalSolver = new GraphicalSolver(optimizationProblem);
        graphicalSolver.findAllIntersections(optimizationProblem.getConstraints());
        return ResponseEntity.ok().build();
    }

}

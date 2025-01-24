package com.mg.lpcalc.controller;

import com.mg.lpcalc.model.OptimizationProblem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/solve")
public class SolveController {

    @PostMapping("/graph")
    public ResponseEntity<?> graphSolve(@RequestBody OptimizationProblem optimizationProblem) {
        Logger.getAnonymousLogger().info(optimizationProblem.toString());
        return ResponseEntity.ok().build();
    }

}

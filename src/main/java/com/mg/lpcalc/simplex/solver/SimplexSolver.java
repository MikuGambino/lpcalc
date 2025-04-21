package com.mg.lpcalc.simplex.solver;

import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Method;
import com.mg.lpcalc.simplex.model.*;
import com.mg.lpcalc.simplex.model.solution.Solution;
import com.mg.lpcalc.simplex.solution.SimplexSolutionBuilder;
import com.mg.lpcalc.simplex.solution.BigMSimplexSolutionBuilder;

import java.util.List;

public class SimplexSolver {
    private List<Constraint> constraints;
    private ObjectiveFunc objectiveFunc;
    private Direction direction;
    private int numVars;
    private int numConstraints;
    private Method method;

    public SimplexSolver(OptimizationProblem problem) {
        this.constraints = problem.getConstraints();
        this.objectiveFunc = problem.getObjectiveFunc();
        this.numVars = constraints.get(0).getCoefficients().size();
        this.numConstraints = constraints.size();
        this.direction = objectiveFunc.getDirection();
        this.method = problem.getMethod();
    }

    public Solution solve() {
        SimplexMethod simplexMethod = null;
        if (method.equals(Method.BASIC)) {
            SimplexSolutionBuilder solutionBuilder = new SimplexSolutionBuilder(constraints, direction);
            simplexMethod = new BasicSimplexMethod(constraints, objectiveFunc, direction, numVars, numConstraints, solutionBuilder);
        }

        if (method.equals(Method.BIG_M)) {
            BigMSimplexSolutionBuilder solutionBuilder = new BigMSimplexSolutionBuilder(constraints, direction);
            simplexMethod = new BigMMethod(constraints, objectiveFunc, direction, numVars, numConstraints, solutionBuilder);
        }

        return simplexMethod.run();
    }
}

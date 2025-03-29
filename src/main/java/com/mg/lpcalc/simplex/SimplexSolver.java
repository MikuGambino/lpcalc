package com.mg.lpcalc.simplex;

import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.model.enums.Method;
import com.mg.lpcalc.simplex.model.*;

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

    public void solve() {
        SimplexMethod simplexMethod = null;
        if (method.equals(Method.BASIC)) {
            simplexMethod = new BasicSimplexMethod(constraints, objectiveFunc, direction, numVars, numConstraints);
        }

        Answer answer = simplexMethod.run();
    }
}

package com.mg.lpcalc.simplex.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.solution.BigMSolution;
import com.mg.lpcalc.simplex.model.solution.ModifyObjectiveFuncStep;
import com.mg.lpcalc.simplex.model.solution.SimplexTableDTO;
import com.mg.lpcalc.simplex.table.BigMSimplexTable;
import lombok.Data;

import java.util.List;

@Data
public class SimplexSolutionBuilderBigM {
    private SimplexSolutionBuilder builder;
    private BigMSolution solution = new BigMSolution();

    public SimplexSolutionBuilderBigM(List<Constraint> constraints, Direction direction) {
        this.builder = new SimplexSolutionBuilder(constraints, direction);
    }

    public void addSlackVariable(int slackIndex, int constraintIndex, Fraction variable) {
        solution.getAddArtAndSlackVariablesStep().getSlackVariablesIndexes().add(slackIndex);
        solution.getAddArtAndSlackVariablesStep().getSlackConstraintIndexes().add(constraintIndex);
        solution.getAddArtAndSlackVariablesStep().getSlackVariables().add(variable);
    }

    public void addArtificialVariable(int artIndex, int constraintIndex) {
        solution.getAddArtAndSlackVariablesStep().getArtVariablesIndexes().add(artIndex);
        solution.getAddArtAndSlackVariablesStep().getArtConstraintIndexes().add(constraintIndex);
    }

    public void tableInitialized(ObjectiveFunc objectiveFunc, BigMSimplexTable simplexTable) {
        solution.getAddArtAndSlackVariablesStep().setConstraints(builder.getConstraints());
        solution.setObjectiveFunc(objectiveFunc);
        SimplexTableDTO simplexTableDTO = new SimplexTableDTO(simplexTable);
        solution.setInitialSimplexTable(simplexTableDTO);
    }
}

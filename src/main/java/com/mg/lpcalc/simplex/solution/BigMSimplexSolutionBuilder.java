package com.mg.lpcalc.simplex.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.ObjectiveFunc;
import com.mg.lpcalc.simplex.model.solution.*;
import com.mg.lpcalc.simplex.model.solution.bigm.BigMSolution;
import com.mg.lpcalc.simplex.table.BigMSimplexTable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BigMSimplexSolutionBuilder {
    private SimplexSolutionBuilder builder;
    private BigMSolution solution = new BigMSolution();
    private CalculateDeltasStep calculateDeltasStep = new CalculateDeltasStep();

    public BigMSimplexSolutionBuilder(List<Constraint> constraints, Direction direction) {
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

    public void startCalculateDeltas() {
        this.calculateDeltasStep = new CalculateDeltasStep();
    }

    public void tableInitialized(ObjectiveFunc objectiveFunc, BigMSimplexTable simplexTable) {
        solution.getAddArtAndSlackVariablesStep().setConstraints(builder.getConstraints());
        solution.setObjectiveFunc(objectiveFunc);
        SimplexTableDTO simplexTableDTO = new SimplexTableDTO(simplexTable);
        solution.setInitialSimplexTable(simplexTableDTO);
    }

    public void addDeltasProduct(String var1Label, String var2Label, Fraction var1Value, Fraction var2Value) {
        FractionM fractionM1 = new FractionM(var1Value);
        FractionM fractionM2 = new FractionM(var2Value);

        this.calculateDeltasStep.getVarLabels().get(calculateDeltasStep.getVarLabels().size() - 1).addAll(List.of(var1Label, var2Label));
        this.calculateDeltasStep.getVarValues().get(calculateDeltasStep.getVarValues().size() - 1).addAll(List.of(fractionM1, fractionM2));
    }

    public void addDeltasProductWithM(String var1Label, String var2Label, Fraction mValue, Fraction var2Value) {
        FractionM fractionM1 = new FractionM(Fraction.ZERO, mValue);
        FractionM fractionM2 = new FractionM(var2Value);

        this.calculateDeltasStep.getVarLabels().get(calculateDeltasStep.getVarLabels().size() - 1).addAll(List.of(var1Label, var2Label));
        this.calculateDeltasStep.getVarValues().get(calculateDeltasStep.getVarValues().size() - 1).addAll(List.of(fractionM1, fractionM2));
    }

    public void startDeltaColumnCalculating() {
        calculateDeltasStep.getVarLabels().add(new ArrayList<>());
        calculateDeltasStep.getVarValues().add(new ArrayList<>());
    }

    public void addColumnCost(Fraction cost, boolean isM) {
        if (isM) {
            this.calculateDeltasStep.getColumnCost().add(new FractionM(Fraction.ZERO, cost));
        } else {
            this.calculateDeltasStep.getColumnCost().add(new FractionM(cost));
        }
    }

    public void endCalculateDeltas() {
        this.calculateDeltasStep.getColumnCost().add(new FractionM(Fraction.ZERO));
    }

    public void setSimplexTableWithDeltas(SimplexTableDTO simplexTableDTO) {
        solution.setSimplexTableWithDeltas(simplexTableDTO);
        solution.setCalculateDeltasStep(calculateDeltasStep);
    }

    public void addOptimalityCheckStep(boolean optimal) {
        this.solution.setOptimalityCheckStep(builder.createOptimalityCheckStep(optimal));
    }

    public void addPivotStepToAnswer(SimplexTableDTO simplexTableBefore, SimplexTableDTO simplexTableAfter, boolean isOptimal) {
        PivotStep pivotStep = builder.getPivotStep();
        pivotStep.setSimplexTableAfter(simplexTableAfter);
        pivotStep.setSimplexTableBefore(simplexTableBefore);
        FractionM minDelta = new FractionM(pivotStep.getMinDelta().getCore(), simplexTableBefore.getMValues()[pivotStep.getColumn()]);
        pivotStep.setMinDelta(minDelta);
        pivotStep.setCalculateDeltasStep(this.calculateDeltasStep);
        pivotStep.setOptimalityCheckStep(new OptimalityCheckStep(isOptimal, builder.getDirection())); // todo check direction usage
        solution.getPivotSteps().add(pivotStep);
    }

    public void addUnsuccessfulPivotStep(SimplexTableDTO simplexTable) {
        Answer answer = builder.getAnswer();
        PivotStep pivotStep = builder.getPivotStep();
        FractionM minDelta = new FractionM(pivotStep.getMinDelta().getCore(), simplexTable.getMValues()[pivotStep.getColumn()]);
        pivotStep.setSimplexTableBefore(simplexTable);
        pivotStep.setMinDelta(minDelta);
        solution.getPivotSteps().add(pivotStep);
        solution.setAnswer(answer);
    }

    public void answerHasArtVars() {
        solution.setAnswer(new Answer(AnswerType.HAS_ART_VARS));
    }
}

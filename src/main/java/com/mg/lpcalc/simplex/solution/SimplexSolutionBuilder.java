package com.mg.lpcalc.simplex.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.solution.*;
import com.mg.lpcalc.simplex.table.BasicSimplexTable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SimplexSolutionBuilder {
    // todo добавить поле какой метод или отдельный класс для каждого метода
    private Answer answer = new Answer();
    private SimplexTableDTO initialSimplexTable;
    private List<Constraint> constraints;
    private List<Integer> slackVariablesIndexes = new ArrayList<>();
    private List<Integer> constraintsIndexes = new ArrayList<>();
    private int[] slackBasis;
    private List<FindBasisSubStep> findBasisSubSteps = new ArrayList<>();
    private List<RemoveNegativeBStep> negativeBSteps = new ArrayList<>();
    private CalculateDeltasStep calculateDeltasStep;

    public SimplexSolutionBuilder(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public void convertToLessOrEqual(List<Constraint> constraints, boolean constraintsIsChanged) {
        ConstraintTransformStep constraintTransformStep = new ConstraintTransformStep(constraints, constraintsIsChanged);
        answer.setConvertToLessOrEqualStep(constraintTransformStep);
    }

    public void addSlackVariable(int slackIndex, int constraintIndex) {
        slackVariablesIndexes.add(slackIndex);
        constraintsIndexes.add(constraintIndex);
    }

    public void tableInitComplete(BasicSimplexTable simplexTable) {
        ConstraintToEqualityStep step = new ConstraintToEqualityStep(constraints, slackVariablesIndexes, constraintsIndexes);
        this.initialSimplexTable = new SimplexTableDTO(simplexTable);
        answer.setConstraintToEqualityStep(step);
    }

    public void setSlackBasis(int[] basis) {
        this.slackBasis = basis.clone();
    }

    public void addFindBasisSubStep(BasisMethod basisMethod, int column, int row,
                                    SimplexTableDTO simplexTableBefore, BasicSimplexTable simplexTableAfter) {
        SimplexTableDTO simplexTableAfterDTO = new SimplexTableDTO(simplexTableAfter);

        Fraction pivotElement = null;
        if (!basisMethod.equals(BasisMethod.UNIT_COLUMN)) {
            pivotElement = simplexTableBefore.getTableau()[row][column];
        }

        FindBasisSubStep subStep = new FindBasisSubStep(basisMethod, column, row, pivotElement, simplexTableBefore, simplexTableAfterDTO);
        findBasisSubSteps.add(subStep);
    }

    public void basisFound() {
        answer.setFindBasisStep(new FindBasisStep(initialSimplexTable, findBasisSubSteps));
    }

    public void addRemoveNegativeBStep(int column, int row, SimplexTableDTO simplexTableBefore, BasicSimplexTable simplexTableAfter) {
        SimplexTableDTO simplexTableAfterDTO = new SimplexTableDTO(simplexTableAfter);
        Fraction maxNegativeB = simplexTableBefore.getTableau()[row][simplexTableBefore.getNumColumns() - 1];
        Fraction maxNegativeRowElement = simplexTableBefore.getTableau()[row][column];
        int oldBasis = simplexTableBefore.getBasis()[row];
        RemoveNegativeBStep step = new RemoveNegativeBStep(column, row, oldBasis, simplexTableBefore,
                simplexTableAfterDTO, maxNegativeB, maxNegativeRowElement);
        answer.getRemoveNegativeBSteps().add(step);
    }

    public void setSimplexTableWithDeltas(SimplexTableDTO simplexTableDTO) {
        answer.setSimplexTableWithDeltas(simplexTableDTO);
        answer.setCalculateDeltasStep(calculateDeltasStep);
    }

    public void startCalculateDeltas() {
        this.calculateDeltasStep = new CalculateDeltasStep();
    }

    public void addDeltasProduct(String var1Label, String var2Label, Fraction var1Value, Fraction var2Value) {
        this.calculateDeltasStep.getVarLabels().get(calculateDeltasStep.getVarLabels().size() - 1).addAll(List.of(var1Label, var2Label));
        this.calculateDeltasStep.getVarValues().get(calculateDeltasStep.getVarValues().size() - 1).addAll(List.of(var1Value, var2Value));
    }

    public void addColumnCost(Fraction cost) {
        this.calculateDeltasStep.getColumnCost().add(cost);
    }

    public void startDeltaColumnCalculating() {
        calculateDeltasStep.getVarLabels().add(new ArrayList<>());
        calculateDeltasStep.getVarValues().add(new ArrayList<>());
    }

    public void endCalculateDeltas() {
        this.calculateDeltasStep.getColumnCost().add(Fraction.ZERO);
    }
}

package com.mg.lpcalc.simplex.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.simplex.model.solution.*;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.table.BasicSimplexTable;
import com.mg.lpcalc.simplex.table.SimplexTable;
import lombok.Getter;
import lombok.Setter;

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
}

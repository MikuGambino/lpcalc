package com.mg.lpcalc.simplex.solution;

import com.mg.lpcalc.model.Fraction;
import com.mg.lpcalc.model.enums.Direction;
import com.mg.lpcalc.simplex.model.Constraint;
import com.mg.lpcalc.simplex.model.solution.*;
import com.mg.lpcalc.simplex.table.BasicSimplexTable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SimplexSolutionBuilder {
    // todo добавить поле какой метод или отдельный класс для каждого метода
    private Solution solution = new Solution();
    private Direction direction;
    private List<Constraint> constraints;
    private SimplexTableDTO initialSimplexTable;
    private List<Integer> slackVariablesIndexes = new ArrayList<>();
    private List<Integer> constraintsIndexes = new ArrayList<>();
    private int[] slackBasis;
    private List<FindBasisSubStep> findBasisSubSteps = new ArrayList<>();
    private List<RemoveNegativeBStep> negativeBSteps = new ArrayList<>();
    private CalculateDeltasStep calculateDeltasStep;
    private PivotStep pivotStep;
    private List<Fraction> simplexRelations;
    private Fraction targetQ;

    public SimplexSolutionBuilder(List<Constraint> constraints, Direction direction) {
        this.constraints = constraints;
        this.direction = direction;
    }

    public void convertToLessOrEqual(List<Constraint> constraints, boolean constraintsIsChanged) {
        ConstraintTransformStep constraintTransformStep = new ConstraintTransformStep(constraints, constraintsIsChanged);
        solution.setConvertToLessOrEqualStep(constraintTransformStep);
    }

    public void addSlackVariable(int slackIndex, int constraintIndex) {
        slackVariablesIndexes.add(slackIndex);
        constraintsIndexes.add(constraintIndex);
    }

    public void tableInitComplete(BasicSimplexTable simplexTable) {
        ConstraintToEqualityStep step = new ConstraintToEqualityStep(constraints, slackVariablesIndexes, constraintsIndexes);
        this.initialSimplexTable = new SimplexTableDTO(simplexTable);
        solution.setConstraintToEqualityStep(step);
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
        solution.setFindBasisStep(new FindBasisStep(initialSimplexTable, findBasisSubSteps));
    }

    public void addRemoveNegativeBStep(int column, int row, SimplexTableDTO simplexTableBefore, BasicSimplexTable simplexTableAfter) {
        SimplexTableDTO simplexTableAfterDTO = new SimplexTableDTO(simplexTableAfter);
        Fraction maxNegativeB = simplexTableBefore.getTableau()[row][simplexTableBefore.getNumColumns() - 1];
        Fraction maxNegativeRowElement = simplexTableBefore.getTableau()[row][column];
        int oldBasis = simplexTableBefore.getBasis()[row];
        RemoveNegativeBStep step = new RemoveNegativeBStep(column, row, oldBasis, simplexTableBefore,
                simplexTableAfterDTO, maxNegativeB, maxNegativeRowElement, true);
        solution.getRemoveNegativeBSteps().add(step);
    }

    public void addUnsuccessfulNegativeBStep(int row, SimplexTableDTO simplexTableBefore) {
        Fraction maxNegativeB = simplexTableBefore.getTableau()[row][simplexTableBefore.getNumColumns() - 1];
        RemoveNegativeBStep step = RemoveNegativeBStep.builder()
                .maxNegativeB(maxNegativeB)
                .simplexTableBefore(simplexTableBefore)
                .row(row)
                .success(false)
                .build();
        solution.getRemoveNegativeBSteps().add(step);
        solution.setAnswer(new Answer(AnswerType.NEGATIVE_B));
    }

    public void setSimplexTableWithDeltas(SimplexTableDTO simplexTableDTO) {
        solution.setSimplexTableWithDeltas(simplexTableDTO);
        solution.setCalculateDeltasStep(calculateDeltasStep);
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

    public void addOptimalityCheckStep(boolean optimal) {
        this.solution.setOptimalityCheckStep(new OptimalityCheckStep(optimal, direction));
    }

    public void saveSimplexRelations(List<Fraction> simplexRelations, Fraction targetQ) {
        this.simplexRelations = simplexRelations;
        this.targetQ = targetQ;
    }

    public void addPivotStep(int row, int column, SimplexTableDTO simplexTableBefore) {
        Fraction[][] tableau = simplexTableBefore.getTableau();
        Fraction pivotElement = tableau[row][column];
        Fraction minimumDelta = tableau[simplexTableBefore.getNumConstraints()][column];
        int lastBasisVariableIndex = simplexTableBefore.getBasis()[row];

        List<Fraction> columnCoefficients = new ArrayList<>();
        List<Fraction> bCoefficients = new ArrayList<>();
        for (int i = 0; i < simplexTableBefore.getNumConstraints(); i++) {
            columnCoefficients.add(tableau[i][column]);
            bCoefficients.add(tableau[i][simplexTableBefore.getNumColumns() - 1]);
        }

        this.pivotStep = PivotStep.builder()
                .simplexTableBefore(simplexTableBefore)
                .pivotElement(pivotElement)
                .minDelta(minimumDelta)
                .targetQ(targetQ)
                .column(column)
                .row(row)
                .lastBasisVariableIndex(lastBasisVariableIndex)
                .simplexRelations(simplexRelations)
                .bCoefficients(bCoefficients)
                .columnCoefficients(columnCoefficients)
                .success(true)
                .build();
    }

    public void addUnsuccessfulPivotStep(int column, Direction direction, SimplexTableDTO simplexTable) {
        Fraction[][] tableau = simplexTable.getTableau();
        Fraction minimumDelta = tableau[simplexTable.getNumConstraints()][column];

        PivotStep pivotStep = PivotStep.builder()
                .column(column)
                .success(false)
                .direction(direction)
                .minDelta(minimumDelta)
                .simplexTableBefore(simplexTable)
                .build();

        this.solution.getPivotSteps().add(pivotStep);

        if (Direction.MAX.equals(direction)) {
            solution.setAnswer(new Answer(AnswerType.MAX_UNBOUNDED));
        } else {
            solution.setAnswer(new Answer(AnswerType.MIN_UNBOUNDED));
        }
    }

    public void addPivotStepToAnswer(SimplexTableDTO simplexTableAfterDTO, boolean isOptimal) {
        this.pivotStep.setSimplexTableAfter(simplexTableAfterDTO);
        this.pivotStep.setCalculateDeltasStep(this.calculateDeltasStep);
        this.pivotStep.setOptimalityCheckStep(new OptimalityCheckStep(isOptimal, direction));
        this.solution.getPivotSteps().add(pivotStep);
    }
}

package com.mg.lpcalc.graphical.model.solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphicalAnswer {
    private GraphicalSolutionType graphicalSolutionType;
    private PointSolution pointSolution;
    private SegmentSolution segmentSolution;
    private RaySolution raySolution;
    private String answer;

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType, PointSolution pointSolution, String answer) {
        this.graphicalSolutionType = graphicalSolutionType;
        this.pointSolution = pointSolution;
        this.answer = answer;
    }

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType, SegmentSolution segmentSolution, String answer) {
        this.graphicalSolutionType = graphicalSolutionType;
        this.segmentSolution = segmentSolution;
        this.answer = answer;
    }

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType, RaySolution raySolution, String answer) {
        this.graphicalSolutionType = graphicalSolutionType;
        this.raySolution = raySolution;
        this.answer = answer;
    }

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType) {
        this.graphicalSolutionType = graphicalSolutionType;
    }
}

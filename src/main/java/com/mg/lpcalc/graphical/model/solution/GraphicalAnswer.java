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

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType, PointSolution pointSolution) {
        this.graphicalSolutionType = graphicalSolutionType;
        this.pointSolution = pointSolution;
    }

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType, SegmentSolution segmentSolution) {
        this.graphicalSolutionType = graphicalSolutionType;
        this.segmentSolution = segmentSolution;
    }

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType, RaySolution raySolution) {
        this.graphicalSolutionType = graphicalSolutionType;
        this.raySolution = raySolution;
    }

    public GraphicalAnswer(GraphicalSolutionType graphicalSolutionType) {
        this.graphicalSolutionType = graphicalSolutionType;
    }
}

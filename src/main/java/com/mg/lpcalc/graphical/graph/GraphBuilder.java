package com.mg.lpcalc.graphical.graph;

import com.mg.lpcalc.graphical.model.graph.*;
import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.ObjectiveFunc;
import com.mg.lpcalc.graphical.model.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GraphBuilder {
    private final double EPS = 1e-9;
    private final int GRAPH_SIZE = 500;
    private final double PADDING_PERCENTAGE = 0.2;
    private GraphParams graphParams;
    private List<Graph> graphs = new ArrayList<>();

    public GraphBuilder(List<Point> points) {
        initGraphParams(points);
    }

    private void initGraphParams(List<Point> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Список точек не может быть пустым");
        }

        double maxX = points.get(0).getX();
        double maxY = points.get(0).getY();
        double minX = points.get(0).getX();
        double minY = points.get(0).getY();

        for (Point point : points) {
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
        }

        double xRange = maxX - minX;
        double yRange = maxY - minY;

        double maxRange = Math.max(xRange, yRange);

        double viewBoxSize = GRAPH_SIZE / 2.;
        // Масштаб внутри ViewBox
        double pxSize = viewBoxSize / maxRange;
        // Учёт отступа с каждой стороны графика
        pxSize -= pxSize * PADDING_PERCENTAGE;
        // Отступ по краям
        double paddingPx = (viewBoxSize * PADDING_PERCENTAGE) / 2;

        ViewBoxParams viewBoxParams = new ViewBoxParams(minX, minY, viewBoxSize, pxSize, paddingPx);

        this.graphParams = GraphParams.builder()
                .maxX(maxX)
                .minX(minX)
                .minY(minY)
                .maxY(maxY)
                .paddingPx(paddingPx)
                .pxSize(pxSize)
                .viewBoxParams(viewBoxParams)
                .graphSize(GRAPH_SIZE)
                .build();
    }

    public String addConstraint(Constraint constraint, List<Point> feasibleRegion, List<Point> axisPoints) {
        Line line = getLineByConstraint(constraint);

        List<Line> graphLines = new ArrayList<>();
        // Сбор линий прошлых графиков
        if (!this.graphs.isEmpty()) {
            Graph lastGraph = this.graphs.get(graphs.size() - 1);
            graphLines = new ArrayList<>(lastGraph.getLines());
        }
        graphLines.add(line);

        // Преобразование координат в пиксели
        List<Point> feasibleRegionPx = feasibleRegionPointsToPx(feasibleRegion, graphLines);
        // Сортировка точек для построения многоугольника
        List<Point> sortedFeasibleRegion = sortPolygonPoints(feasibleRegionPx);
        Polygon polygon = new Polygon(sortedFeasibleRegion);

        List<Circle> feasibleRegionPoints = pointsToCircles(feasibleRegion);
        List<Circle> axisCircles = pointsToCircles(axisPoints).stream().distinct().toList();

        Graph newGraph = new Graph(graphParams, graphLines, polygon, feasibleRegionPoints, axisCircles);
        this.graphs.add(newGraph);
        return newGraph.toSVG();
    }

    public Line getLineByConstraint(Constraint constraint) {
        List<Point> points = new ArrayList<>();
        ViewBoxParams viewBox = graphParams.getViewBoxParams();
        double minX = toCords(viewBox.getMinX() - viewBox.getSize());
        double minY = toCords(viewBox.getMinY() - viewBox.getSize());
        double maxX = toCords(viewBox.getMinX());
        double maxY = toCords(viewBox.getMinY());

        if (constraint.getB() != 0) {
            double yAtMinX = (-constraint.getA() * minX + constraint.getC()) / constraint.getB();
            points.add(toPx(new Point(minX, yAtMinX)));

            double yAtMaxX = (-constraint.getA() * maxX + constraint.getC()) / constraint.getB();
            points.add(toPx(new Point(maxX, yAtMaxX)));
        }
        if (constraint.getA() != 0) {
            double xAtMinY  = (-constraint.getB() * minY + constraint.getC()) / constraint.getA();
            points.add(toPx(new Point(xAtMinY, minY)));

            double xAtMaxY = (-constraint.getB() * maxY + constraint.getC()) / constraint.getA();
            points.add(toPx(new Point(xAtMaxY, maxY)));
        }

        return new Line(points.get(0), points.get(1));
    }

    // Сортировка точек многоугольника по углу
    private List<Point> sortPolygonPoints(List<Point> points) {
        if (points.size() < 3) {
            return points;
        }

        Point center = findCentroid(points);

        List<Point> sortedPoints = new ArrayList<>(points);
        sortedPoints.sort(Comparator.comparingDouble(p -> Math.atan2(p.getY() - center.getY(), p.getX() - center.getX())));

        return sortedPoints;
    }

    private Point findCentroid(List<Point> points) {
        double sumX = 0;
        double sumY = 0;

        for (Point p : points) {
            sumX += p.getX();
            sumY += p.getY();
        }

        return new Point(
                sumX / points.size(),
                sumY / points.size()
        );
    }

    private List<Circle> pointsToCircles(List<Point> points) {
        List<Circle> circles = new ArrayList<>();
        for (Point point : points) {
            if (point.isUnbounded()) continue;
            Point pxPoint = toPx(point);
            Circle circle = new Circle(pxPoint.getX(), pxPoint.getY(), point.getX(), point.getY());
            circles.add(circle);
        }

        return circles;
    }

    private List<Point> feasibleRegionPointsToPx(List<Point> points, List<Line> lines) {
        ViewBoxParams viewBox = graphParams.getViewBoxParams();

        // Точка которая находится за пределами графика
        // и точка, выше которой располагается ОДР в данном алгоритме равнозначны
        // Чтобы не писать дублирующего кода, "feasibleRegionIsAbove" точки делаем также Unbounded
        List<Point> pCopies = new ArrayList<>();
        for (Point point : points) {
            Point pointCopy = new Point(point);
            if (point.isFeasibleRegionIsAbove()) {
                pointCopy.setUnbounded(true);
            }
            pCopies.add(pointCopy);
        }

        List<Point> pointsPx = new ArrayList<>();
        Point cornerPoint = null;

        for (Point point : pCopies) {
            Point pxPoint = toPx(point);
            // Если точка не лежит в пределах видимого графика, то точка добавляется в массив,
            // и происходит переход к следующей
            if (!point.isUnbounded()) {
                pointsPx.add(pxPoint);
                continue;
            }

            // Если точка на границе графика, нужно определить координаты новой точки, учитывая отступ графика
            // Вычисление координат происходит путём поиска прямой, на которой лежит точка
            Line line = findLineContainingPoint(lines, pxPoint);
            if (line != null && line.getEndPoint().getY() >= 0) { // Проверка на то, что точка в I четверти
                pointsPx.add(line.getEndPoint());
                pointsPx.add(pxPoint);
                continue;
            }

            // Если точка имеет бесконечную координату X и Y, то она потенциально может являться угловой (maxX && maxY)
            if (point.isUnbounded() && point.getX() != 0 && point.getY() != 0) {
                cornerPoint = new Point(viewBox.getMinX(), viewBox.getMinY());
                continue;
            }

            // Если точка не находится на прямой, значит она лежит на оси координат
            // В таком случае, для точки, которая лежит за пределами графика:
            // в качестве координат точки используется край видимой области (ViewBox)
            Point newPoint = null;

            if (point.getY() == 0) {
                newPoint = new Point(viewBox.getMinX(), pxPoint.getY());
            }
            if (point.getX() == 0) {
                newPoint = new Point(pxPoint.getX(), viewBox.getMinY());
            }

            if (newPoint != null) {
                pointsPx.add(newPoint);
            }

            pointsPx.add(pxPoint);
         }

        // Если имеется "угловая точка", нужно проверить действительно ли она является таковой
        // Для этого нужно узнать, есть ли точка правее и выше угловой
        boolean isCorner = true;
        if (cornerPoint != null) {
            for (Point point : pointsPx) {
                if (point.getY() >= cornerPoint.getY() && point.getX() >= cornerPoint.getX()) {
                    isCorner = false;
                    break;
                }
            }
            if (isCorner) {
                pointsPx.add(cornerPoint);
            }
        }

        return pointsPx;
    }

    // Функция для поиска прямой, на которой находится точка
    public Line findLineContainingPoint(List<Line> lines, Point point) {
        for (Line line : lines) {
            if (isPointOnLine(point, line)) {
                return line;
            }
        }

        return null;
    }

    // Функция для проверки, находится ли точка на прямой
    public boolean isPointOnLine(Point p, Line line) {
        Point a = line.getBeginPoint();
        Point b = line.getEndPoint();

        double crossProduct = (b.getX() - a.getX()) * (p.getY() - a.getY())
                - (b.getY() - a.getY()) * (p.getX() - a.getX());
        return !(Math.abs(crossProduct) > 1e-9);
    }

    public String addObjectiveFunc(ObjectiveFunc objectiveFunc) {
        ViewBoxParams viewBox = graphParams.getViewBoxParams();
        double minX = viewBox.getMinX() - viewBox.getSize();
        double minY = viewBox.getMinY() - viewBox.getSize();

        double a = objectiveFunc.getA();
        double b = objectiveFunc.getB();

        double endX = a * graphParams.getPxSize();
        double endY = b * graphParams.getPxSize();

        // Если функция выходит за график
        if (objectiveFuncNotInScale(objectiveFunc)) {
            if (endX <= endY) {
                endX = minX + 5;
                endY = (objectiveFunc.getA() * endX) / objectiveFunc.getB();
                endY = objectiveFunc.getB() > 0 ? Math.abs(endY) : -Math.abs(endY);
            } else {
                endY = minY + 5;
                endX = (objectiveFunc.getA() * endY) / objectiveFunc.getB();
                endX = objectiveFunc.getA() > 0 ? Math.abs(endX) : -Math.abs(endX);
            }
        }

        Arrow objectiveFuncArrow = new Arrow(endX, endY);

        Graph lastGraph = this.graphs.get(graphs.size() - 1);
        lastGraph.setObjectiveFunc(objectiveFuncArrow);

        return lastGraph.toSVG();
    }

    public boolean objectiveFuncNotInScale(ObjectiveFunc objectiveFunc) {
        ViewBoxParams viewBox = graphParams.getViewBoxParams();
        double minX = viewBox.getMinX() - viewBox.getSize();
        double minY = viewBox.getMinY() - viewBox.getSize();

        double endX = objectiveFunc.getA() * graphParams.getPxSize();
        double endY = objectiveFunc.getB() * graphParams.getPxSize();

        return endX < minX || endY < minY;
    }

    public String getFinalGraph(ObjectiveFunc objectiveFunc, List<Point> optimalPoints) {
        if (optimalPoints.isEmpty()) {
            return this.graphs.get(graphs.size() - 1).toSVG();
        }
        boolean unboundedMin = optimalPoints.size() == 1 && optimalPoints.get(0).isFeasibleRegionIsAbove();
        Point answerPoint = optimalPoints.get(0);
        double a = objectiveFunc.getA();
        double b = objectiveFunc.getB();
        double c = a * answerPoint.getX() + b * answerPoint.getY();
        if (unboundedMin) {
            c -= 1;
            optimalPoints = new ArrayList<>();
        }

        // уравнение прямой через нормальный вектор
        Constraint perpendicularConstraint = new Constraint(a, b, c);
        Line perpendicularLine = getLineByConstraint(perpendicularConstraint);
        perpendicularLine.setStrokeWidth(0.7);
        List<Circle> answerCircles = pointsToCircles(optimalPoints);
        Graph lastGraph = this.graphs.get(graphs.size() - 1);
        lastGraph.setPerpendicularLine(perpendicularLine);
        lastGraph.setOptimalPoints(answerCircles);
        return lastGraph.toSVG();
    }

    // Преобразования координат в пиксели
    private Point toPx(Point point) {
        return new Point(point.getX() * graphParams.getPxSize(), point.getY() * graphParams.getPxSize());
    }

    // Преобразование пикселей в координаты
    private double toCords(Double num) {
        return num / graphParams.getPxSize();
    }
}

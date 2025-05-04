package com.mg.lpcalc.graphical.model.solution;

import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.graphical.model.ObjectiveFunc;
import com.mg.lpcalc.graphical.model.Point;
import com.mg.lpcalc.model.enums.Operator;
import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class LatexParser {

    public static String parseConstraint(double a, double b, double c, Operator operator) {
        Constraint constraint = new Constraint(a, b, c, operator);
        return parseConstraint(constraint);
    }

    public static String parseConstraint(Constraint constraint) {
        return new StringBuilder()
                .append(termToLatex(constraint.getA(), "x_1", false))
                .append(termToLatex(constraint.getB(), "x_2", constraint.getA() != 0))
                .append(operatorToLatex(constraint.getOperator()))
                .append(numToLatex(constraint.getC(), false))
                .toString();
    }

    public static String numToLatex(double num, boolean needPlusSign) {
        if (num < 0) {
            return "-%s".formatted(formatNumber(Math.abs(num)));
        } else if (needPlusSign){
            return "+%s".formatted(formatNumber(num));
        } else {
            return "%s".formatted(formatNumber(num));
        }
    }

    public static String termToLatex(double num, String variable, boolean needPlusSign) {
        if (num == 0) {
            return "";
        }

        if (Math.abs(num) == 1) {
            if (num == -1) {
                return "-%s".formatted(variable);
            } else {
                if (needPlusSign) {
                    return "+%s".formatted(variable);
                } else {
                    return "%s".formatted(variable);
                }
            }
        }

        if (num < 0) {
            return "-%s%s".formatted(formatNumber(Math.abs(num)), variable);
        }

        if (needPlusSign) {
            return "+%s%s".formatted(formatNumber(num), variable);
        }

        return "%s%s".formatted(formatNumber(num), variable);
    }

    public static String operatorToLatex(Operator operator) {
        if (operator.equals(Operator.GEQ)) return " \\geq " ;
        if (operator.equals(Operator.LEQ)) return " \\leq " ;
        return " = ";
    }


    public static String formatNumber(double number) {
        double rounded = Math.round(number * 100) / 100.0;

        if (rounded == Math.floor(rounded)) {
            return String.valueOf((int)rounded);
        }

        return String.format(Locale.US, "%.2f", rounded)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }

    private static StringBuilder parseSystemOfEquations(List<Constraint> constraints) {
        Constraint c1 = constraints.get(1);
        Constraint c2 = constraints.get(0);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\begin{cases}");
        stringBuilder.append(parseConstraint(c1.getA(), c1.getB(), c1.getC(), Operator.EQ)).append("\\\\");
        stringBuilder.append(parseConstraint(c2.getA(), c2.getB(), c2.getC(), Operator.EQ));
        stringBuilder.append("\\end{cases}");
        return stringBuilder;
    }

    private static StringBuilder parseSystemOfX(Point point) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\begin{cases}");
        stringBuilder.append(parseConstraint(1, 0, point.getX(), Operator.EQ)).append("\\\\");
        stringBuilder.append(parseConstraint(0, 1, point.getY(), Operator.EQ));
        stringBuilder.append("\\end{cases}");
        return stringBuilder;
    }

    public static String parseFindXFromSystem(List<Constraint> constraints, Point point) {
        return new StringBuilder("$")
                .append(parseSystemOfEquations(constraints))
                .append("\\quad \\Rightarrow \\quad")
                .append(parseSystemOfX(point))
                .append("$")
                .toString();
    }

    public static String parseObjectiveFunc(ObjectiveFunc objectiveFunc, Point point, double value) {
        return new StringBuilder("$F = ")
                .append(parseMultiply(objectiveFunc.getA(), point.getX(), false))
                .append(parseMultiply(objectiveFunc.getB(), point.getY(), true))
                .append(" = ").append(formatNumber(value)).append("$")
                .toString();
    }

    private static String parseMultiply(double num1, double num2, boolean needPlusSignInBegin) {
        StringBuilder stringBuilder = new StringBuilder();
        if (needPlusSignInBegin && num1 > 0) {
            stringBuilder.append("+");
        }

        stringBuilder.append(formatNumber(num1))
                     .append("\\cdot");

        if (num2 < 0) {
            stringBuilder.append("(").append(formatNumber(num2)).append(")");
        } else {
            stringBuilder.append(formatNumber(num2));
        }

        return stringBuilder.toString();
    }

    public static String parseParamSystemOfEquation(Point point, double t1, double t2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\begin{cases}");
        stringBuilder.append(parseConstraint(1, 0, point.getX(), Operator.EQ)).append(termToLatex(t1, "t", true)).append("\\\\");
        stringBuilder.append(parseConstraint(0, 1, point.getY(), Operator.EQ)).append(termToLatex(t2, "t", true));
        stringBuilder.append("\\end{cases}");
        return stringBuilder.toString();
    }
}

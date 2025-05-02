package com.mg.lpcalc.graphical.model.solution;

import com.mg.lpcalc.graphical.model.Constraint;
import com.mg.lpcalc.model.enums.Operator;
import lombok.Data;

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
}

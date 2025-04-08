document.addEventListener("DOMContentLoaded", function() {
    renderMathInElement(document.body, {
        delimiters: [
            {left: "$$", right: "$$", display: true},
            {left: "$", right: "$", display: false}
        ]
    });
});

function printInput(input) {
    document.getElementById('func-dir').innerText = input.objective.direction == "MAX" ? "наибольшее" : "наименьшее";
    // Отрисовка целевой функции
    let objectiveFunc = parseLHS(input.objective.coefficients);
    updateMathFormula('objFuncInput', 'F = ' + objectiveFunc);

    // Отрисовка ограничений
    let constraints = '';
    for (let i = 0; i < input.constraints.length; i++) {
        let constraint = input.constraints[i];
        constraints += parseLHS(constraint.coefficients) + parseRHS(constraint.operator, constraint.rhs) + '\\\\';
    }
    constraints = '\\begin{cases}' + constraints + '\\end{cases}';
    updateMathFormula('constraintsInput', constraints);
}

function fractionToLatex(fraction) {
    if (fraction.denominator == 1) {
        return fraction.numerator;
    }
    return `\\frac{${fraction.numerator}}{${fraction.denominator}}`;
}

function updateMathFormula(elementId, formula, displayMode = false) {
    const element = document.getElementById(elementId);
    element.innerHTML = '';
    katex.render(formula, element, {
        displayMode: displayMode
    });
}

function parseLHS(equation) {
    let constraintFormula = '';
    for (let i = 0; i < equation.length; i++) {
        let coeff = equation[i];
        if (coeff.numerator == 0) continue;
        let sign = '';
        if (constraintFormula.length != 0 && coeff.numerator >= 0) sign = "+";
        if (coeff.numerator < 0) {
            sign = "-";
            coeff.numerator = Math.abs(coeff.numerator);
        }
        let num = fractionToLatex(coeff);
        num = num == 1 ? '' : num;
        constraintFormula += sign + num + 'x_' + (i + 1);
    }
    return constraintFormula;
}

function parseRHS(operator, num) {
    let output = '';
    if (operator == 'LEQ' || operator == 'GEQ') {
        output += "\\" + operator.toLowerCase();
    } else {
        output += "=";
    }
    if (num.numerator < 0) output += "-";
    output += fractionToLatex(num);
    return output;
} 


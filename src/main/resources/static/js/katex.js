document.addEventListener("DOMContentLoaded", function() {
    renderMathInElement(document.body, {
        delimiters: [
            {left: "$$", right: "$$", display: true},
            {left: "$", right: "$", display: false}
        ]
    });
});

function renderKatexElement(elementId) {
    renderMathInElement(document.getElementById(elementId), {
        delimiters: [
          {left: '$', right: '$', display: false},
          {left: '$$', right: '$$', display: true}
        ],
        throwOnError: false
    });
}

function fractionToLatex(fraction) {
    if (fraction.denominator == 1) {
        return fraction.numerator;
    }

    if (fraction.numerator < 0) {
        return `-\\frac{${Math.abs(fraction.numerator)}}{${fraction.denominator}}`;
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
    output += fractionToLatex(num);
    return output;
} 

function addColoredVariable(constraint, variableTitle, variableIndex, sign, color) {
    return constraint + sign + `\\colorbox{${color}}{$${variableTitle + "_" + variableIndex}$}`;
}
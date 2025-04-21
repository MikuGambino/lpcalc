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

function mDeltaToFraction(core, mValue) {
    return {
        core: { numerator: core.numerator, denominator: core.denominator },
        m: { numerator: mValue.numerator, denominator: mValue.denominator }
    };
}

function fractionToLatex(fraction) {
    if ('m' in fraction) {
        return mFractionToLatex(fraction);
    }
    if (fraction.denominator == 1) {
        return fraction.numerator;
    }

    if (fraction.numerator < 0) {
        return `-\\frac{${Math.abs(fraction.numerator)}}{${fraction.denominator}}`;
    }
    return `\\frac{${fraction.numerator}}{${fraction.denominator}}`;
}

function mFractionToLatex(fraction) {
    if (fraction.m.numerator == 0) {
        return fractionToLatex(fraction.core);
    } else if (fraction.core.numerator == 0) {
        if (fraction.m.numerator == 1) {
            return "M";
        } else if (fraction.m.numerator == -1) {
            return "-M";
        }
        return fractionToLatex(fraction.m) + "M";
    } else if (fraction.m.numerator > 0) {
        let fractionM = fractionToLatex(fraction.m) + "M";
        if (fraction.m.numerator == 1) {
            fractionM = 'M';
        }
        return fractionToLatex(fraction.core) + "+" + fractionM;
    } else {
        let fractionM = fractionToLatex(fraction.m) + "M";
        if (fraction.m.numerator == -1) {
            fractionM = '-M';
        }
        return fractionToLatex(fraction.core) + "" + fractionM;
    }
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
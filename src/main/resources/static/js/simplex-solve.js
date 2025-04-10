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
    document.getElementById('input-block').hidden = false;
}

function parseBasicSimplexAnswer(answer) {
    parseConvertToLessOrEqual(answer.convertToLessOrEqualStep.constraints, answer.convertToLessOrEqualStep.constraintsIsChanged);
    document.getElementById('solution-container').hidden = false;
}

function parseConvertToLessOrEqual(constraints, isChanged) {
    let doneTitle = document.getElementById('lessOrEqualDone');
    let transformBlock = document.getElementById('lessOrEqualTransform');
    if (!isChanged) {
        doneTitle.hidden = false;
        transformBlock.hidden = true;
        return;
    }

    doneTitle.hidden = true;
    let constraintsLatex = '';
    for (let i = 0; i < constraints.length; i++) {
        let constraint = constraints[i];
        constraintsLatex += parseLHS(constraint.coefficients) + parseRHS(constraint.operator, constraint.rhs) + '\\\\';
    }
    constraintsLatex = '\\begin{cases}' + constraintsLatex + '\\end{cases}';
    updateMathFormula('lessOrEqualsConstraints', constraintsLatex);
    transformBlock.hidden = false;
}
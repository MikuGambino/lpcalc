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
    let constraintToEqualityStep = answer.constraintToEqualityStep;
    parseConstraintToEqualityStep(constraintToEqualityStep.constraints, constraintToEqualityStep.constraintsIndexes, constraintToEqualityStep.slackVariablesIndexes);
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

function parseConstraintToEqualityStep(constraints, constraintsIndexes, slackVariablesIndexes) {
    document.getElementById('slackVariablesListP').hidden = false;
    document.getElementById('noSlackVariablesTitle').hidden = true;
    let constraintsLHS = [];

    if (slackVariablesIndexes.length == 0) {
        document.getElementById('slackVariablesListP').hidden = true;
        document.getElementById('noSlackVariablesTitle').hidden = false;
    } else if (slackVariablesIndexes.length == 1) {
        document.getElementById('slackVariablesListSpan').innerHTML = parseValueWithIndex('x', slackVariablesIndexes[0]);
    } else if (slackVariablesIndexes.length == 2) {
        document.getElementById('slackVariablesListSpan').innerHTML = parseValueWithIndex('x', slackVariablesIndexes[0]) 
        + ' и ' + parseValueWithIndex('x', slackVariablesIndexes[1]);
    } else {
        document.getElementById('slackVariablesListSpan').innerHTML = parseValueWithIndex('x', slackVariablesIndexes[0]) 
        + '...' + parseValueWithIndex('x', slackVariablesIndexes[slackVariablesIndexes.length - 1]);
    }
    
    console.log(constraints);
    for (let i = 0; i < constraints.length; i++) {
        let constraint = constraints[i];
        constraintsLHS.push(parseLHS(constraint.coefficients));
    }
    console.log(constraintsLHS);

    for (let i = 0; i < constraintsIndexes.length; i++) {
        let constraintIndex = constraintsIndexes[i];
        constraintsLHS[constraintIndex] = addColoredVariable(constraintsLHS[constraintIndex], 'x', slackVariablesIndexes[i], '+', '#F17C3A');
    }
    console.log(constraintsLHS);

    let constraintsLatex = '';
    for (let i = 0; i < constraintsLHS.length; i++) {
        let constraint = constraints[i];
        constraintsLatex += constraintsLHS[i] + parseRHS('EQ', constraint.rhs) + '\\\\';
    }
    constraintsLatex = '\\begin{cases}' + constraintsLatex + '\\end{cases}';
    updateMathFormula('equalityConstraints', constraintsLatex);
}
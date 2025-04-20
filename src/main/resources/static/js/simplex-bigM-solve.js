function parseBigMSimplexAnswer(solution) {
    let solutionContainer = document.getElementById('solution-container');
    solutionContainer.innerHTML = '';
    let algoTitle = document.createElement('h2');
    algoTitle.innerText = 'Решение методом искуственного базиса (BigM)';
    solutionContainer.appendChild(algoTitle);

    let addSlackAndArtStep = parseAddSlackAndArtVariables(solution.addArtAndSlackVariablesStep);
    solutionContainer.appendChild(createP('Шаг 1. Преобразование ограничений к равенствам и добавление искуственных переменных.', 'subtitle'));
    solutionContainer.appendChild(addSlackAndArtStep);

    activateAccordions();
    renderKatexElement('solution-container');
    renderKatexElement('input-block');
}

function parseAddSlackAndArtVariables(step) {
    const { constraints, slackConstraintIndexes, slackVariablesIndexes, slackVariables, artVariablesIndexes, artConstraintIndexes } = step;

    let container = document.createElement('div');
    container.id = 'addSlackAndArtVariables';

    container.appendChild(createP('Для каждого неравенства добавляется дополнительная переменная ($x$), которая называется балансовой.'));
    container.appendChild(createP('Если неравенство вида $\\geq$, балансовая переменная добавляется со знаком минус.'));

    let constraintsLHS = [];

    if (slackVariablesIndexes.length == 0) {
        container.appendChild(createP('Все ограничения являются равенствами, дополнительные переменные не нужны.'));
    }

    container.appendChild(createP('Для ограничений вида $\\geq$ и $=$ добавляется дополнительная переменная ($u$), которая называется искусственной.'));
    if (artVariablesIndexes.length == 0) {
        container.appendChild(createP('Все ограничения имеют вид $\\leq$, искусственные переменные не нужны.'));
    }

    for (let i = 0; i < constraints.length; i++) {
        let constraint = constraints[i];
        constraintsLHS.push(parseLHS(constraint.coefficients));
    }

    for (let i = 0; i < slackConstraintIndexes.length; i++) {
        let constraintIndex = slackConstraintIndexes[i];
        let sign = '+';
        if (slackVariables[i].numerator < 0) {
            sign = '-';
        }
        constraintsLHS[constraintIndex] = addColoredVariable(constraintsLHS[constraintIndex], 'x', slackVariablesIndexes[i], sign, '#f2bb92');
    }

    for (let i = 0; i < artVariablesIndexes.length; i++) {
        let constraintIndex = artConstraintIndexes[i];
        constraintsLHS[constraintIndex] = addColoredVariable(constraintsLHS[constraintIndex], 'u', artVariablesIndexes[i], '+', '#f2bb92');
    }

    let constraintsLatex = '';
    for (let i = 0; i < constraintsLHS.length; i++) {
        let constraint = constraints[i];
        constraintsLatex += constraintsLHS[i] + parseRHS('EQ', constraint.rhs) + '\\\\';
    }
    container.appendChild(createP('$\\begin{cases}' + constraintsLatex + '\\end{cases}$', 'latex'));
    container.appendChild(createP('Добавленные переменные выделены оранжевым цветом.'));
    return container;
}
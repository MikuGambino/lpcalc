function parseBigMSimplexAnswer(solution) {
    let solutionContainer = document.getElementById('solution-container');
    solutionContainer.innerHTML = '';
    let algoTitle = document.createElement('h2');
    algoTitle.innerText = 'Решение методом искуственного базиса (BigM)';
    solutionContainer.appendChild(algoTitle);

    let addSlackAndArtStep = parseAddSlackAndArtVariables(solution.addArtAndSlackVariablesStep);
    solutionContainer.appendChild(createP('Шаг 1. Преобразование ограничений к равенствам и добавление искуственных переменных.', 'subtitle'));
    solutionContainer.appendChild(addSlackAndArtStep);

    let modifyObjectiveFuncStep = parseModifyObjectiveFuncStep(solution.objectiveFunc, solution.initialSimplexTable);
    solutionContainer.appendChild(createP('Шаг 2. Модификация целевой функции.', 'subtitle'));
    solutionContainer.appendChild(modifyObjectiveFuncStep);

    let findBasisStep = parseFindBasisStepBigM(solution.initialSimplexTable, solution.objectiveFunc);
    solutionContainer.appendChild(createP('Шаг 3. Поиск первоначального базиса.', 'subtitle'));
    solutionContainer.appendChild(findBasisStep);

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

function parseModifyObjectiveFuncStep(objectiveFunc, initialTable) {
    let artVariablesCount = initialTable.numColumns - initialTable.numSlack - initialTable.numVars - 1;

    let container = document.createElement('div');
    container.id = 'modifyObjectiveFuncStep';

    if (artVariablesCount == 0) {
        container.appendChild(createP('Искуственные переменные не были добавлены. Модификация не нужна.'));
        return container;
    }

    let objectiveLatex = '$F = ' + parseLHS(objectiveFunc.coefficients);
    let sign;
    if (objectiveFunc.direction == 'MAX') {
        sign = '-';
        container.appendChild(createP('В целевую функцию добавляем искусственные пременные с коэффициентом $-M$, где $M$ — очень большое число.'));
    } else {
        container.appendChild(createP('В целевую функцию добавляем искусственные пременные с коэффициентом $M$, где $M$ — очень большое число.'));
        sign = '+';
    }

    for (let i = 0; i < artVariablesCount; i++) {
        objectiveLatex += sign + 'Mu_' + (i + 1) + '$';
    }

    container.appendChild(createP(objectiveLatex, 'latex'));
    return container;
}

function parseFindBasisStepBigM(table, objectiveFunc) {
    let container = document.createElement('div');
    container.id = 'findBasisStep';
    
    let artVariablesMinNumber = table.numSlack + table.numVars;
    
    for (let i = 0; i < table.basis.length; i++) {
        let basisVarIdx = table.basis[i];
        if (basisVarIdx >= artVariablesMinNumber) {
            container.appendChild(createP(`Ограничение ${i + 1} содержит искусственную переменную $u_${basisVarIdx-artVariablesMinNumber+1}$. Она входит в базис.`));
        } else {
            container.appendChild(createP(`Ограничение ${i + 1} содержит положительную балансовую переменную $x_${basisVarIdx+1}$. Она входит в базис.`));
        }
    }

    let simplexTableHTML = parseSimplexTable(table, container);
    modifySimplexTableBigM(simplexTableHTML, table, objectiveFunc);
    container.appendChild(createP('Базиз успешно найден.'));
    return container;
}

function modifySimplexTableBigM(tableHTML, simplexTable, objectiveFunc, withQ = 0) {
    let artVariablesMinNumber = simplexTable.numSlack + simplexTable.numVars;
    let basis = simplexTable.basis;

    const secondRow = tableHTML.rows[1];
    if (secondRow) {
        for (let i = 1; i < secondRow.cells.length - 1 - withQ; i++) {
            if (i > artVariablesMinNumber) {
                secondRow.cells[i].textContent = `$u_${i - artVariablesMinNumber}$`;
                if (objectiveFunc.direction == 'MAX') {
                    tableHTML.rows[0].cells[i].textContent = '$-M$'; 
                } else {
                    tableHTML.rows[0].cells[i].textContent = '$M$'; 
                }
            }
        }
    }
    
    const rows = tableHTML.rows;
    for (let i = 2; i < rows.length; i++) {
        if (basis[i - 2] >= artVariablesMinNumber) {
            rows[i].cells[0].textContent = `$u_${basis[i - 2] - artVariablesMinNumber + 1}$`;
        }
    }
}
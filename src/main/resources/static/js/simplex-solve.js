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
    parseFindBasisStep(answer.findBasisStep);
    parseRemoveNegativeBSteps(answer.removeNegativeBSteps);
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
    
    for (let i = 0; i < constraints.length; i++) {
        let constraint = constraints[i];
        constraintsLHS.push(parseLHS(constraint.coefficients));
    }

    for (let i = 0; i < constraintsIndexes.length; i++) {
        let constraintIndex = constraintsIndexes[i];
        constraintsLHS[constraintIndex] = addColoredVariable(constraintsLHS[constraintIndex], 'x', slackVariablesIndexes[i], '+', '#f2bb92');
    }

    let constraintsLatex = '';
    for (let i = 0; i < constraintsLHS.length; i++) {
        let constraint = constraints[i];
        constraintsLatex += constraintsLHS[i] + parseRHS('EQ', constraint.rhs) + '\\\\';
    }
    constraintsLatex = '\\begin{cases}' + constraintsLatex + '\\end{cases}';
    updateMathFormula('equalityConstraints', constraintsLatex);
}

function parseFindBasisStep(step) {
    const container = document.getElementById('findBasisStepContainer');
    container.innerHTML = '';

    for(let i = 0; i < step.slackBasisTable.basis.length; i++) {
        let basisIndex = step.slackBasisTable.basis[i];
        if (basisIndex == -1) continue;
        let slackBasisTitle = document.createElement('p');
        console.log(basisIndex);
        slackBasisTitle.innerText = `Ограничение ${i + 1} содержит балансовую переменную $x_${basisIndex + 1}$. Она входит в базис.`
        container.appendChild(slackBasisTitle);
    }

    parseSimplexTable(step.slackBasisTable, container);

    for (let i = 0; i < step.subSteps.length; i++) {
        let hrHTML = document.createElement('hr');
        container.appendChild(hrHTML);
        let subStep = step.subSteps[i];
        let beforeBasisAddingTitle = document.createElement('p');
        let afterBasisAddingTitle = document.createElement('p');
        if (subStep.method == 'GAUSS') {
            beforeBasisAddingTitle.innerText = `Для ограничения $${subStep.row + 1}$ найдём первый столбец, который ещё не входит в базис.\n` + 
                                               `Таким столбцом является $${subStep.column + 1}$ столбец.\n` +
                                               `Для того, чтобы столбец стал базисным, необходимо преобразовать его в единичный столбец, ` + 
                                               `где в строке $${subStep.row + 1}$ будет единица, а в остальных строках - нули.\n`;

            afterBasisAddingTitle.innerText = `Делим строку $${subStep.row + 1}$ на $${fractionToLatex(subStep.pivotElement)}$. Из остальных строк вычитаем строку $${subStep.row + 1}$, умноженную на соответствующий элемент в столбце $${subStep.column + 1}$.\n` +
                                              `Базисной переменной для ограничения $${subStep.row + 1}$ становится $x_${subStep.column + 1}$.`;
        } else if (subStep.method == 'SINGLE_NONZERO') {
            beforeBasisAddingTitle.innerText = `Для ограничения $${subStep.row + 1}$ найдём столбец, в котором одно число является ненулевым.\n` + 
                                               `Таким столбцом является $${subStep.column + 1}$ столбец.\n` +
                                               `Для того, чтобы столбец стал базисным, необходимо преобразовать его в единичный столбец, ` + 
                                               `где в строке $${subStep.row + 1}$ будет единица, а в остальных строках - нули.\n`;
        
            afterBasisAddingTitle.innerText = `Делим строку $${subStep.row + 1}$ на $${fractionToLatex(subStep.pivotElement)}$.\n` +
                                              `Базисной переменной для ограничения $${subStep.row + 1}$ становится $x_${subStep.column + 1}$.`;
        } else if (subStep.method == 'UNIT_COLUMN') {
            beforeBasisAddingTitle.innerText = `Для ограничения $${subStep.row + 1}$ найдём столбец, в котором одно число является $1$, а другие равны $0$.\n` +
                                               `Таким столбцом является $${subStep.column + 1}$ столбец.\n`;

            afterBasisAddingTitle.innerText = `Базисной переменной для ограничения $${subStep.row + 1}$ становится $x_${subStep.column + 1}$.`;
        }

        container.appendChild(beforeBasisAddingTitle);
        let beforeTable = parseSimplexTable(subStep.simplexTableBefore, container);
        highlightRowColumnAndIntersection(beforeTable, subStep.row + 2, subStep.column + 1);
        container.appendChild(afterBasisAddingTitle);
        parseSimplexTable(subStep.simplexTableAfter, container);
    }

    let basisFoundP = document.createElement('p');
    basisFoundP.innerText = 'Базиз успешно найден.';
    container.appendChild(basisFoundP);
    
    renderKatexElement('findBasisStepContainer');
}

function parseSimplexTable(simplexTable, container) {
    const { tableau, costs, basis, numColumns } = simplexTable;
    let table = document.createElement('table');
    table.className = 'simplex-table';
    
    const columnLabels = [];
    for (let i = 0; i < numColumns - 1; i++) {
        columnLabels.push(`x_{${i + 1}}`);
    }
    columnLabels.push('b');

    let tableHTML = '<table class="simplex-table" border="1" cellspacing="0" cellpadding="8">';

    tableHTML += '<tr><th>C</th>';
    for (let i = 0; i < costs.length; i++) {
        tableHTML += `<td>$${fractionToLatex(costs[i])}$</td>`;
    }
    tableHTML += '</tr>';

    tableHTML += '<tr><th>Базис</th>';
    for (let i = 0; i < columnLabels.length; i++) {
      tableHTML += `<th>$${columnLabels[i]}$</th>`;
    }
    tableHTML += '</tr>';

    for (let i = 0; i < tableau.length - 1; i++) { // Минус 1, потому что последняя строка - дельта
        tableHTML += '<tr>';
        
        // Первая колонка - переменная в базисе
        if (basis[i] != -1) {
            tableHTML += `<th>$x_{${basis[i] + 1}}$</th>`;
        } else {
            tableHTML += `<th>$?$</th>`;
        }
        
        
        // Значения в ячейках
        for (let j = 0; j < tableau[i].length; j++) {
          const value = tableau[i][j];
          tableHTML += `<td>$${fractionToLatex(value)}$</td>`;
        }
        tableHTML += '</tr>';
    }

    // Последняя строка - дельта
    // tableHTML += '<tr>';
    // tableHTML += '<th>$\\Delta$</th>';
    // const lastRowIndex = tableau.length - 1;
    // for (let j = 0; j < tableau[lastRowIndex].length; j++) {
    //     tableHTML += `<td>$${formatFractionLatex(tableau[lastRowIndex][j])}$</td>`;
    // }
    // tableHTML += '</tr>';
    
    tableHTML += '</table>';
    
    table.innerHTML = tableHTML;
    container.appendChild(table);
    return table;
}

function highlightRowColumnAndIntersection(table, rowIndex, columnIndex) {
    const rows = table.rows;
        
    for (let i = 2; i < rows.length; i++) {
        if (rows[i] && rows[i].cells[columnIndex]) {
            rows[i].cells[columnIndex].style.backgroundColor = 'rgba(247, 166, 104, 0.7)';
        }
    }
    
    if (rows[rowIndex]) {
        for (let j = 1; j < rows[rowIndex].cells.length; j++) {
            rows[rowIndex].cells[j].style.backgroundColor = 'rgba(247, 166, 104, 0.7)';
        }
    }
    
    if (rows[rowIndex] && rows[rowIndex].cells[columnIndex]) {
        rows[rowIndex].cells[columnIndex].style.backgroundColor = 'rgba(241, 124, 58, 0.7)';
    }
}

function parseRemoveNegativeBSteps(steps) {
    let container = document.getElementById('removeNegativeBStepContainer');
    container.innerHTML = '';

    if (steps.length == 0) {
        let noNegativeBTitle = document.createElement('p');
        noNegativeBTitle.innerText = 'Отрицательных свободных коэффициентов нет.'
        container.appendChild(noNegativeBTitle);
    }

    for (let i = 0; i < steps.length; i++) {
        if (i != 0) {
            let hrHTML = document.createElement('hr');
            container.appendChild(hrHTML);
        }
        let step = steps[i];
        let beforeSimplexTableP = document.createElement('p');
        beforeSimplexTableP.innerText = 'В столбце $b$ присутствуют отрицательные значения.'
        container.appendChild(beforeSimplexTableP);

        let beforeTable = parseSimplexTable(step.simplexTableBefore, container);
        highlightRowColumnAndIntersection(beforeTable, step.row + 2, step.column + 1);

        let afterSimplexTableP = document.createElement('p');
        afterSimplexTableP.innerText = `Максимальный по модулю свободный коэффициент среди отрицательных $${fractionToLatex(step.maxNegativeB)}$ находится в строке $${step.row + 1}$.\n` +
                                       `Максимальное по модулю число среди отрицательных в строке $${step.row + 1} = ${fractionToLatex(step.maxNegativeRowElement)}$.\n` +
                                       `Заменяем базисную переменную $x_${step.oldBasis + 1}$ на $x_${step.column + 1}$.\n` + 
                                       `Делим строку $${step.row + 1}$ на $${fractionToLatex(step.maxNegativeRowElement)}$. Из остальных строк вычитаем строку $${step.row + 1}$, умноженную на соответствующий элемент в столбце $${step.column + 1}$.`
        container.appendChild(afterSimplexTableP);

        parseSimplexTable(step.simplexTableAfter, container);
    }

    renderKatexElement('removeNegativeBStepContainer');
}
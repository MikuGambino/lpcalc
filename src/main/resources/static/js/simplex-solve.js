let objectiveFunc;

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

function parseBasicSimplexAnswer(solution) {
    objectiveFunc = solution.objectiveFunc;
    let solutionContainer = document.getElementById('solution-container');
    solutionContainer.innerHTML = '';
    let algoTitle = document.createElement('h2');
    algoTitle.innerText = 'Решение базовым симлекс методом';
    solutionContainer.appendChild(algoTitle);

    let inputBlock = document.getElementById('input-block');
    removeAnswerContainers();

    inputBlock.appendChild(parseAnswer(solution.answer));
    if (solution.answer.answerType == 'NO_BASIS') {
        renderKatexElement('input-block');
        document.querySelector(".solve-container").hidden = false;
        algoTitle.innerText = '';
        return;
    }

    let lessOrEqualContainer = parseConvertToLessOrEqual(solution.convertToLessOrEqualStep);
    solutionContainer.appendChild(createP('Шаг 1. Преобразование неравенств со знаком $\\geq$.', 'subtitle'));
    solutionContainer.appendChild(lessOrEqualContainer);

    let toEqualStep = parseConstraintToEqualityStep(solution.constraintToEqualityStep);
    solutionContainer.appendChild(createP('Шаг 2. Преобразование ограничений к равенствам.', 'subtitle'));
    solutionContainer.appendChild(toEqualStep);

    let findBasisStep = parseFindBasisStep(solution.findBasisStep);
    solutionContainer.appendChild(createP('Шаг 3. Поиск первоначального базиса.', 'subtitle'));
    solutionContainer.appendChild(findBasisStep);

    let removeNegativeBStep = parseRemoveNegativeBSteps(solution.removeNegativeBSteps);
    solutionContainer.appendChild(createP('Шаг 4. Исключение отрицательных свободных коэффициентов.', 'subtitle'));
    solutionContainer.appendChild(removeNegativeBStep);

    if (solution.answer.answerType != 'NEGATIVE_B') {
        let deltaStep = parseCalculatingDeltasStep(solution.simplexTableWithDeltas, solution.calculateDeltasStep);
        solutionContainer.appendChild(createP('Шаг 5. Расчёт дельт (оценок).', 'subtitle'));
        solutionContainer.appendChild(deltaStep);

        let checkOptimalityStep = parseCheckOptimalityStep(solution.optimalityCheckStep);
        solutionContainer.appendChild(createP('Шаг 6. Проверка оптимальности.', 'subtitle'));
        solutionContainer.appendChild(checkOptimalityStep);

        if (solution.optimalityCheckStep.optimal == false) {
            let simplexIterationsStep = parsePivotIterationsStep(solution.pivotSteps, solution.answer);
            solutionContainer.appendChild(createP('Шаг 7. Итерации симплекс-алгоритма.', 'subtitle'));
            solutionContainer.appendChild(simplexIterationsStep);
        }
    }

    solutionContainer.appendChild(parseAnswer(solution.answer));
    activateAccordions();
    renderKatexElement('solution-container');
    renderKatexElement('input-block');
    document.querySelector(".solve-container").hidden = false;
}

function parseConvertToLessOrEqual(step) {
    const { constraints, constraintsIsChanged } = step;
    
    let container = document.createElement('div');
    container.id = 'lessOrEqualStep';

    container.appendChild(createP('Все ограничения должны быть со знаком $\\leq$ или $=$.'));

    if (!constraintsIsChanged) {
        container.appendChild(createP('Условие выполнено.'));
        return container;
    }

    container.appendChild(createP('Меняем знаки у ограничений с $\\geq$, путём умножения на $-1$:'));
    let constraintsLatex = '';
    for (let i = 0; i < constraints.length; i++) {
        let constraint = constraints[i];
        constraintsLatex += parseLHS(constraint.coefficients) + parseRHS(constraint.operator, constraint.rhs) + '\\\\';
    }

    container.appendChild(createP('$\\begin{cases}' + constraintsLatex + '\\end{cases}$', 'latex'));
    return container;
}

function parseConstraintToEqualityStep(step) {
    const { constraints, constraintsIndexes, slackVariablesIndexes } = step;

    let container = document.createElement('div');
    container.id = 'toEqualityStep';

    container.appendChild(createP('Для каждого неравенства добавляется дополнительная переменная, которая называется балансовой.'));

    let constraintsLHS = [];
    let slackVariables = 'Добавленные переменные: ';

    if (slackVariablesIndexes.length == 0) {
        container.appendChild(createP('Все ограничения являются равенствами, дополнительные переменные не нужны.'));
    } else if (slackVariablesIndexes.length == 1) {
        slackVariables += `$x_${slackVariablesIndexes[0]}$.`;
    } else if (slackVariablesIndexes.length == 2) {
        slackVariables += `$x_${slackVariablesIndexes[0]}$ и $x_${slackVariablesIndexes[1]}$.`;
    } else {
        slackVariables += `$x_${slackVariablesIndexes[0]}$...$x_${slackVariablesIndexes[slackVariablesIndexes.length - 1]}$.`;
    }

    container.appendChild(createP(slackVariables));
    
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
    container.appendChild(createP('$\\begin{cases}' + constraintsLatex + '\\end{cases}$', 'latex'));
    container.appendChild(createP('Добавленные переменные выделены оранжевым цветом.'));
    return container;
}

function parseFindBasisStep(step) {
    const container = document.createElement('div');
    container.id = 'findBasisStepContainer';

    for(let i = 0; i < step.slackBasisTable.basis.length; i++) {
        let basisIndex = step.slackBasisTable.basis[i];
        if (basisIndex == -1) continue;
        container.appendChild(createP(`Ограничение ${i + 1} содержит балансовую переменную $x_${basisIndex + 1}$. Она входит в базис.`));
    }

    container.appendChild(parseSimplexTable(step.slackBasisTable));

    for (let i = 0; i < step.subSteps.length; i++) {
        container.appendChild(document.createElement('hr'));
        const { simplexTableBefore, simplexTableAfter, method, row, column, pivotElement } = step.subSteps[i];
        let beforeBasisAddingTitle = document.createElement('p');
        let afterBasisAddingTitle = document.createElement('p');
        if (method == 'GAUSS') {
            beforeBasisAddingTitle.innerText = `Для ограничения $${row + 1}$ найдём первый столбец, который ещё не входит в базис.\n` + 
                                               `Таким столбцом является $${column + 1}$ столбец.\n` +
                                               `Для того, чтобы столбец стал базисным, необходимо преобразовать его в единичный столбец, ` + 
                                               `где в строке $${row + 1}$ будет единица, а в остальных строках - нули.\n`;

            afterBasisAddingTitle.innerText = `Делим строку $${row + 1}$ на $${fractionToLatex(pivotElement)}$. Из остальных строк вычитаем строку $${row + 1}$, умноженную на соответствующий элемент в столбце $${column + 1}$.\n` +
                                              `Базисной переменной для ограничения $${row + 1}$ становится $x_${column + 1}$.`;
        } else if (method == 'SINGLE_NONZERO') {
            beforeBasisAddingTitle.innerText = `Для ограничения $${row + 1}$ найдём столбец, в котором одно число является ненулевым.\n` + 
                                               `Таким столбцом является $${column + 1}$ столбец.\n` +
                                               `Для того, чтобы столбец стал базисным, необходимо преобразовать его в единичный столбец, ` + 
                                               `где в строке $${row + 1}$ будет единица, а в остальных строках - нули.\n`;
        
            afterBasisAddingTitle.innerText = `Делим строку $${row + 1}$ на $${fractionToLatex(pivotElement)}$.\n` +
                                              `Базисной переменной для ограничения $${row + 1}$ становится $x_${column + 1}$.`;
        } else if (method == 'UNIT_COLUMN') {
            beforeBasisAddingTitle.innerText = `Для ограничения $${row + 1}$ найдём столбец, в котором одно число является $1$, а другие равны $0$.\n` +
                                               `Таким столбцом является $${column + 1}$ столбец.\n`;

            afterBasisAddingTitle.innerText = `Базисной переменной для ограничения $${row + 1}$ становится $x_${column + 1}$.`;
        }

        container.appendChild(beforeBasisAddingTitle);
        let beforeTable = parseSimplexTable(simplexTableBefore);
        highlightRowColumnAndIntersection(beforeTable, row + 2, column + 1);
        container.appendChild(beforeTable);
        container.appendChild(afterBasisAddingTitle);
        container.appendChild(parseSimplexTable(simplexTableAfter));
    }

    container.appendChild(createP('Базиз успешно найден.'));
    return container;
}

function parseSimplexTable(simplexTable, withDeltas = false) {
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

    if (withDeltas) {
        let mValuesPresent = simplexTable.mvalues != null;
        tableHTML += '<tr>';
        tableHTML += '<th>$\\Delta$</th>';
        const lastRowIndex = tableau.length - 1;
        for (let j = 0; j < tableau[lastRowIndex].length; j++) {
            let tableText = '';
            if (mValuesPresent) {
                tableText = `$${fractionToLatex(mDeltaToFraction(tableau[lastRowIndex][j], simplexTable.mvalues[j]))}$`;
            } else {
                tableText = `$${fractionToLatex(tableau[lastRowIndex][j])}$`;
            }
            tableHTML += `<td>${tableText}</td>`;
        }
        tableHTML += '</tr>';
    }

    tableHTML += '</table>';
    
    table.innerHTML = tableHTML;
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
    let container = document.createElement('div');
    container.id = 'removeNegativeBStepContainer';

    if (steps.length == 0) {
        container.appendChild(createP('Отрицательных свободных коэффициентов нет.'));
    }

    for (let i = 0; i < steps.length; i++) {
        if (i != 0) {
            container.appendChild(document.createElement('hr'));
        }
        const { success, simplexTableBefore, simplexTableAfter, row, column, oldBasis, maxNegativeRowElement, maxNegativeB } = steps[i];

        let beforeSimplexTableP = document.createElement('p');
        beforeSimplexTableP.innerText = 'В столбце $b$ присутствуют отрицательные значения.'
        container.appendChild(beforeSimplexTableP);

        let beforeTable = parseSimplexTable(simplexTableBefore);
        highlightRowColumnAndIntersection(beforeTable, row + 2, column + 1);
        container.appendChild(beforeTable);

        let afterSimplexTableP = document.createElement('p');

        if (!success) {
            afterSimplexTableP.innerText = `Максимальный по модулю свободный коэффициент среди отрицательных $${fractionToLatex(maxNegativeB)}$ находится в строке $${row + 1}$.\n` + 
                                           `В строке $${row + 1}$ отсутствуют отрицательные значения. Решения задачи не существует.`;
            
            container.appendChild(afterSimplexTableP);
            return container;
        }

        afterSimplexTableP.innerText = `Максимальный по модулю свободный коэффициент среди отрицательных $${fractionToLatex(maxNegativeB)}$ находится в строке $${row + 1}$.\n` +
                                       `Максимальное по модулю число среди отрицательных в строке $${row + 1} = ${fractionToLatex(maxNegativeRowElement)}$.\n` +
                                       `Заменяем базисную переменную $x_${oldBasis + 1}$ на $x_${column + 1}$.\n` + 
                                       `Делим строку $${row + 1}$ на $${fractionToLatex(maxNegativeRowElement)}$. Из остальных строк вычитаем строку $${row + 1}$, умноженную на соответствующий элемент в столбце $${column + 1}$.`
        container.appendChild(afterSimplexTableP);

        container.appendChild(parseSimplexTable(simplexTableAfter));
    }

    return container;
}

function parseCalculatingDeltasStep(simplexTable, calculateDeltasStep) {
    let container = document.createElement('div');
    container.id = 'deltaCalculatingStep';

    let tableHTML = parseSimplexTable(simplexTable, true);
    if (simplexTable.mvalues != null) {
        modifySimplexTableBigM(tableHTML, simplexTable);
    }

    let deltas = getDeltasFromTable(tableHTML);
    let deltasAccordion = parseDeltasCalculationsAccordion(calculateDeltasStep, deltas, simplexTable.basis);
    container.appendChild(deltasAccordion);
    
    container.appendChild(tableHTML);
    return container;
}

function parseDeltasCalculationsAccordion(step, deltas, basis) {
    let container = document.createElement('div');

    let formula = document.createElement('p');
    formula.innerText = "Вычисляем дельты: $\\Delta_i = "

    for (let i = 0; i < basis.length; i++) {
        formula.innerText += `C_{${basis[i] + 1}} \\cdot a_{${i + 1}i}`;
        if (i != basis.length - 1) {
            formula.innerText += "+";
        }
    }
    formula.innerText += '- C_i$';

    container.appendChild(formula);

    let spanTrigger = document.createElement('span');
    spanTrigger.className = 'link accordion-trigger';

    let spanIndicator = document.createElement('span');
    spanIndicator.innerHTML = '<span class="indicator">&#x25B6;</span>Подробный расчёт дельт';
    spanTrigger.appendChild(spanIndicator);

    let accordionContent = document.createElement('div');
    accordionContent.className = 'accordion-content';

    container.appendChild(spanTrigger);
    container.appendChild(accordionContent);

    for (let i = 0; i < step.columnCost.length; i++) {
        let p = document.createElement('p');
        p.innerText = `$\\Delta_{${i + 1}} = `;
        
        for (let j = 0; j < step.varLabels[i].length - 1; j += 2) {
            p.innerText += step.varLabels[i][j] + " \\cdot " + step.varLabels[i][j + 1];
            if (j + 2 < step.varLabels.length - 1) {
                p.innerText += " + ";
            }
        }

        p.innerText += ' - C_' + (i + 1) + " = ";

        for (let j = 0; j < step.varValues[i].length - 1; j += 2) {
            if (j != 0) {
                if (step.varValues[i][j].moreOrEqualZero) {
                    p.innerText += " + ";
                }
            }
            if (step.varValues[i][j + 1].moreOrEqualZero) {
                p.innerText += fractionToLatex(step.varValues[i][j]) + " \\cdot " + fractionToLatex(step.varValues[i][j + 1]);
            } else {
                p.innerText += fractionToLatex(step.varValues[i][j]) + " \\cdot (" + fractionToLatex(step.varValues[i][j + 1]) + ")";
            }
        }

        let sign = '-';
        if (!step.columnCost[i].moreOrEqualZero) {
            sign = '';
        }
        p.innerText += sign + fractionToLatex(step.columnCost[i]) + ' = ' + deltas[i] + "$";
        accordionContent.appendChild(p);
    }

    return container;
}

function parseCheckOptimalityStep(step) {
    let container = document.createElement('div');
    container.id = 'optimalityCheckStep';

    let optimality = parseCheckOptimality(step);
    container.appendChild(optimality);

    return container;
}

function parseCheckOptimality(step) {
    let container = document.createElement('div');

    let checkP = document.createElement('p');
    let criteriaP = document.createElement('p');

    if (step.direction == 'MAX') {
        criteriaP.innerText = 'План оптимален, если в таблице отсутствуют отрицательные дельты.';
        if (step.optimal) {
            checkP.innerHTML = 'Проверка оптимальности: отрицательные дельты отсутствуют. <b>План оптимален.</b>';
        } else {
            checkP.innerHTML = 'Проверка оптимальности: <b>план не оптимален</b>, так как присутствуют отрицательные дельты.';
        }
    } else {
        criteriaP.innerText = 'План оптимален, если в таблице отсутствуют положительные дельты.';
        if (step.optimal) {
            checkP.innerHTML = 'Проверка оптимальности: положительные дельты отсутствуют. <b>План оптимален.</b>';
        } else {
            checkP.innerHTML = 'Проверка оптимальности: <b>план не оптимален</b>, так как присутствуют положительные дельты.';
        }
    }

    container.appendChild(criteriaP);
    container.appendChild(checkP);
    return container;
}

function parsePivotIterationsStep(pivotSteps, answer) {
    let container = document.createElement('div');
    container.innerHTML = '';

    for (let i = 0; i < pivotSteps.length; i++) {
        let step = pivotSteps[i];
        container.appendChild(createP(`Итерация ${i + 1}.`, 'subtitle'));
        if (step.success) {
            let optimalCheck = parseCheckOptimality(step.optimalityCheckStep);
            container.appendChild(parsePivotIteration(step, optimalCheck));
        } else {
            container.appendChild(parsePivotIteration(step, null));
        }
    }

    return container;
}

function parsePivotIteration(step, optimalCheck) {
    let container = document.createElement('div');

    let beforeTableP = document.createElement('p');
    let afterTableP = document.createElement('p');

    if (step.success == false) {
        container.appendChild(parseSimplexTable(step.simplexTableBefore, true));
        if (step.direction == 'MAX') {
            beforeTableP.innerText += `Определяем стобец, в котором находится минимальная дельта.\n` + 
            `Минимальная дельта: $${fractionToLatex(step.minDelta)}$. Разрешающий столбец: $${step.column + 1}$.\n` + 
            `Все значения столбца $${step.column + 1}$ неположительны.\n` + 
            `На позиции $${step.column + 1}$ в строке стоимостей $(C)$ стоит положительный элемент.\n` + 
            `Следовательно, функция неограниченно возрастает.`; 
        } else {
            beforeTableP.innerText += `Определяем стобец, в котором находится минимальная дельта.\n` + 
            `Минимальная дельта: $${fractionToLatex(step.minDelta)}$. Разрешающий столбец: $${step.column + 1}$.\n` + 
            `Все значения столбца $${step.column + 1}$ неположительны.\n` + 
            `На позиции $${step.column + 1}$ в строке стоимостей $(С)$ стоит отрицательный элемент.\n` + 
            `Следовательно, функция неограниченно убывает.`;
        }
        
        container.appendChild(beforeTableP);
        return container;
    }

    beforeTableP.innerText += 'Определяем стобец, в котором находится минимальная дельта.\n' + 
                              `Минимальная дельта: $${fractionToLatex(step.minDelta)}$. Разрешающий столбец: $${step.column + 1}$.\n` + 
                              `Находим симплекс-отношения ($Q$), путём деления свободных коэффициентов ($b$) на соответствующие неотрицательные значения столбца $${step.column + 1}$.\n` +
                              'В найденном столбце ищем минимальное неотрицательное значение сиплекс-отношения ($Q$).\n' +
                              `Минимальное значение симплекс-отношения $Q_{min}=${fractionToLatex(step.targetQ)}$. Разрешающая строка: $${step.row + 1}$.\n` +
                              `На пересечении найденных столбца и строки находится разрешающий элемент: $${fractionToLatex(step.pivotElement)}$`;

    container.appendChild(beforeTableP);
    let beforeTable = parseSimplexTable(step.simplexTableBefore, true);
    if (step.simplexTableBefore.mvalues != null) {
        modifySimplexTableBigM(beforeTable, step.simplexTableBefore);
    }
    addQColumn(beforeTable, step);
    highlightRowColumnAndIntersection(beforeTable, step.row + 2, step.column + 1);
    container.appendChild(beforeTable);

    afterTableP.innerText += `Делим строку $${step.row + 1}$ на $${fractionToLatex(step.pivotElement)}$. Из остальных строк вычитаем строку $${step.row + 1}$, умноженную на соответствующий элемент в столбце $${step.column + 1}$.\n` +
                             `Базисной переменной для ограничения $${step.row + 1}$ становится $x_${step.column + 1}$.\n` +
                             `Переменная $x_${step.lastBasisVariableIndex + 1}$ выводится из базиса.`;
    
    container.appendChild(afterTableP);
    let afterTableHTML = parseSimplexTable(step.simplexTableAfter, true);
    if (step.simplexTableAfter.mvalues != null) {
        modifySimplexTableBigM(afterTableHTML, step.simplexTableBefore);
    }
    container.appendChild(afterTableHTML);
    let deltasAccordion = parseDeltasCalculationsAccordion(step.calculateDeltasStep, getDeltasFromTable(afterTableHTML), step.simplexTableAfter.basis);
    container.appendChild(deltasAccordion);
    container.appendChild(optimalCheck);
    return container;
}

function addQColumn(table, step) {  
    const nameRow = table.rows[1];
    const qNameCell = document.createElement('th');
    qNameCell.textContent = '$Q$';
    nameRow.appendChild(qNameCell);
    
    for (let i = 0; i < table.rows.length - 3; i++) {
        const row = table.rows[i + 2];
        const qCell = document.createElement('td');
        if (step.columnCoefficients[i].numerator < 0) {
            qCell.textContent = `$${fractionToLatex(step.bcoefficients[i])}:(${fractionToLatex(step.columnCoefficients[i])}) = ${fractionToLatex(step.simplexRelations[i])}$`; 
        } else {
            qCell.textContent = `$${fractionToLatex(step.bcoefficients[i])}:${fractionToLatex(step.columnCoefficients[i])} = ${fractionToLatex(step.simplexRelations[i])}$`; 
        }
        row.appendChild(qCell);
    }
}

function parseAnswer(answer) {
    let container = document.createElement('div');
    container.className = 'answerContainer';
    let answerP = document.createElement('p');
    let titleP = document.createElement('p');
    titleP.className = 'subtitle';
    titleP.innerText = 'Ответ';

    if (answer.answerType == 'SUCCESS') {
        answerP.innerText = '$';
        for (let i = 0; i < answer.variablesValues.length; i++) {
            answerP.innerText += `x_${i + 1} = ${fractionToLatex(answer.variablesValues[i])},`;
        }
    
        answerP.innerText += `F = ${fractionToLatex(answer.objectiveValue)}$`;
    } else if (answer.answerType == 'NEGATIVE_B') {
        answerP.innerText = 'Решения задачи не существует.';
    } else if (answer.answerType == 'MAX_UNBOUNDED') {
        answerP.innerText = 'Функция неограниченно возрастает. Оптимального решения нет.';
    } else if (answer.answerType == 'MIN_UNBOUNDED') {
        answerP.innerText = 'Функция неограниченно убывает. Оптимального решения нет.';
    } else if (answer.answerType == 'HAS_ART_VARS') {
        answerP.innerText = 'Решения задачи не существует.';
    } else if (answer.answerType == 'NO_BASIS') {
        answerP.innerText = 'Решения задачи не существует. Ограничения противоречат друг другу.';
    }

    container.appendChild(titleP);
    container.appendChild(answerP);
    return container;
}

function getDeltasFromTable(table) {
    const lastRow = table.rows[table.rows.length - 1];
    let deltas = [];
    for (let i = 1; i < lastRow.cells.length; i++) {
        deltas.push(lastRow.cells[i].textContent.slice(1, -1)); // Убираем знаки $$
    }

    return deltas;
}
function activateAccordions() {
    document.querySelectorAll('.accordion-trigger').forEach(trigger => {
        trigger.addEventListener('click', () => {
            const content = trigger.nextElementSibling;
            
            trigger.classList.toggle('open');
            
            if (content.style.maxHeight) {
                content.style.maxHeight = null;
            } else {
                content.style.maxHeight = content.scrollHeight + "px";
            }
        });
    });
}